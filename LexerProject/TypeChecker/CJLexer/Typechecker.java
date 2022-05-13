package CJLexer;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Typechecker {
	
	//Array list of program statements outside of classes
	private final ArrayList<Statement> statements;
	
	//Map of program variable declarations outside of classes
	private final Map<String, VariableDecExp> outsideVariables;
	
	//Map of classes with their respective names
	private Map<String, ClassDefExp> classes;
	
	//Placeholders
	private String currentClass;
	private int currentConstructor;
	private boolean insideConstructor;
	private String currentMethod;
	
	//Pieces of classes respective to the class name
	private Map<String, Map<String, InstanceDecExp>> instances;
	private Map<String, ArrayList<ConstructorDef>> constructors;
	//CVD stands for Constructor Variable Declarations
	private Map<String, ArrayList<Map<String, VariableDecExp>>> CVD;
	private Map<String, Map<String, MethodDefExp>> methods;
	private Map<String, Map<String, Map<String, VariableDecExp>>> variables;
	
	
	//Constructor takes in program and type checks its statements
	public Typechecker(Program program) throws TypeErrorException {
		
		//Initialize
		this.statements = program.statementList;
		this.outsideVariables = new HashMap<String, VariableDecExp>();
		this.classes = new HashMap<String, ClassDefExp>();
		this.instances = new HashMap<String, Map<String, InstanceDecExp>>();
		this.constructors = new HashMap<String, ArrayList<ConstructorDef>>();
		this.CVD = new HashMap<String, ArrayList<Map<String, VariableDecExp>>>();
		this.methods = new HashMap<String, Map<String, MethodDefExp>>();
		this.variables = new HashMap<String, Map<String, Map<String, VariableDecExp>>>();
		
		currentClass = null;
		currentMethod = null;
		insideConstructor = true;
		
		ArrayList<ClassDefExp> classList = program.classDefList;
		
		for (ClassDefExp classy : classList) {
			
			if (classes.containsKey(classy.name)) {
				throw new TypeErrorException("Duplicate class declared: " + classy.name);
			}
			
			this.classes.put(classy.name, classy);
			ArrayList<InstanceDecExp> instanceList = classy.members;
			Map<String, InstanceDecExp> classyDecMap = new HashMap<String, InstanceDecExp>();
			
			for (InstanceDecExp i : instanceList) {
				
				if (classyDecMap.containsKey(i.var.var.name)) {
					throw new TypeErrorException("Duplicate member declared in class " 
					+ classy.name + " : " + i.var.var.name);
				}
				
				classyDecMap.put(i.var.var.name, i);
				this.instances.put(classy.name, classyDecMap);
			}
			
			ArrayList<MethodDefExp> methodList = classy.methods;
			Map<String, MethodDefExp> classyMetMap = new HashMap<String, MethodDefExp>();
			
			for (MethodDefExp method : methodList) {
				
				if (classyMetMap.containsKey(method.name)) {
					throw new TypeErrorException("Duplicate method declared in class " 
				    + classy.name + " : " + method.name);
				}
				
				classyMetMap.put(method.name, method);
				this.methods.put(classy.name, classyMetMap);
				
				ArrayList<VariableDecExp> variableList = method.parameters;
				
				Map<String, VariableDecExp> classyParaMap = new HashMap<String, VariableDecExp>();
				
				for (VariableDecExp v : variableList) {
					
					if (classyParaMap.containsKey(v.var.name)) {
						throw new TypeErrorException("Duplicate parameter declared in class " 
					    + classy.name + " method " + method.name + " : " + v.var.name);
					}
					
					classyParaMap.put(v.var.name, v);
				}
				
				Map<String, Map<String, VariableDecExp>> tempMap = new HashMap<String, Map<String, VariableDecExp>>();
				
				tempMap.put(method.name, classyParaMap);
				this.variables.put(classy.name, tempMap);
			}

			ArrayList<ConstructorDef> constructorList = classy.constructors;
			ArrayList<ConstructorDef> tempConstructor = new ArrayList<ConstructorDef>();
			
			for (int i = 0; i < constructorList.size(); i++) {
				
				for (int j = 0; i < tempConstructor.size(); j++) {
					
					if ((tempConstructor.get(j).parameters.equals(constructorList.get(i).parameters))) {
						throw new TypeErrorException("Duplicate declared constructor: Name: " 
					    + constructorList.get(i).name + "Parameters: " 
						+ constructorList.get(i).parameters.toString());
					}
				}
				
				tempConstructor.add(i, constructorList.get(i));
				this.constructors.put(classy.name, tempConstructor);
				
				ArrayList<VariableDecExp> variableList = constructorList.get(i).parameters;
				Map<String, VariableDecExp> tempMap2 = new HashMap<String, VariableDecExp>();
				
				for (VariableDecExp v : variableList) {
					
					if (tempMap2.containsKey(v.var.name)) {
						throw new TypeErrorException("Duplicate parameter declared in class " + 
					classy.name
						+ " constructor: " + constructorList.get(i).name + "("
						+ constructorList.get(i).parameters.toString() + ")" + " : " + v.var.name);
					}
					
					tempMap2.put(v.var.name, v);
				}
				
				ArrayList<Map<String, VariableDecExp>> tempMap3 = new ArrayList<Map<String, VariableDecExp>>();
				
				tempMap3.add(tempMap2);
				this.CVD.put(classy.name, tempMap3);
			}
		}
	}
		public static void Programcheck(final Program prog) throws TypeErrorException {
		new Typechecker(prog);
	}
	
	public void Classcheck (final ClassDefExp c) throws TypeErrorException {
		// check if class extends another class and check to see if that extending class exists
		if(c.extending==true) {
			if(!ensureClassExists(c.extendingClass)) {
				throw new TypeErrorException("Extending class is non-existent: "+c.extendingClass);
			}
		}
		for(InstanceDecExp i: c.members) {
			Instancecheck(i);
		}
		this.currentClass = c.name;
		for (ConstructorDef cd : c.constructors) {
			Constructorcheck(cd);
		}
		for (MethodDefExp m : c.methods) {
			Methodcheck(m);
		}
	}
	
	public void Instancecheck(final InstanceDecExp i) throws TypeErrorException{ 
		//Checking if type exists if instanceof class object type
		if(i.var.type instanceof ObjectType) {
			Boolean bool = false;
			for(ClassDefExp c: classes.values()) {
				if(c.name.equals(i.var.type.toString())) {
					bool=true; 
				}
				//Check for generic variables if they exist
				if(c instanceof GenericClassDefinition) {
					for(VariableExp v: ((GenericClassDefinition)c).genericList) {
						if(v.name.equals(i.var.type.toString())) {
							bool=true; 
						}
					}
				}
			}
			if(!bool) {
				throw new TypeErrorException("Class type or generic type cannnot be found"
						+ ": "+i.var.type.toString()+" for instance variable: "+i.var.var.name.toString());
			}
		}
	}
	
	// Typechecking for methods
		public void Methodcheck(final MethodDefExp m) throws TypeErrorException {
			this.currentMethod = m.name;
			//Checking if return type exists if instanceof class object type
			if(m.type instanceof ObjectType) {
				Boolean bool = false; 
				for(ClassDefExp c: classes.values()) {
					if(c.name.equals(m.type.toString())) {
						bool=true; 
					}
					//Check for generic variables if they exist
					if(c instanceof GenericClassDefinition) {
						for(VariableExp v: ((GenericClassDefinition)c).genericList) {
							if(v.name.equals(m.type.toString())) {
								bool=true; 
							}
						}
					}
				}
				if(!bool) {
					throw new TypeErrorException("Class type or generic type not found: "+m.type.toString()+" for method: "+ m.name);
				}
			}
			for (Statement s : m.block) {
				typecheckStmt(s);
			}
		}
		public void Constructorcheck(final ConstructorDef cd) throws TypeErrorException {
			inConstructor = true;
			for (int i = 0; i < constructors.get(currentClass).size(); i++) {
				if (constructors.get(currentClass).get(i).equals(cd)) {
					this.currentConstructor = i;
					break;
				}
			}

			for (Statement s : cd.block) {
				if (s instanceof AssignmentStmt) {
					AssignmentStmt as = (AssignmentStmt) s;
					typecheckAssignment(as);
				} else if (s instanceof VariableDecExp) {
					if(((VariableDecExp) s).type instanceof CustomType){
						if(!ensureClassExists(((VariableDecExp) s).type.toString())) {
							throw new TypeErrorException("Class type not found: "+((VariableDecExp)s).type.toString());
						}
					}
					if (constructors.get(this.currentClass).get(currentConstructor).parameters.contains(s))
						throw new TypeErrorException(
								"Variable " + ((VariableDecExp) s).var.name + " already declared in parameters");
					else {
						constructorVariableDec.get(currentClass).get(currentConstructor).put(((VariableDecExp) s).var.name,
								((VariableDecExp) s));
					}
				}
			}
			inConstructor = false;
		}
}
