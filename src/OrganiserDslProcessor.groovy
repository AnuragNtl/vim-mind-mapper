
import static java.util.Calendar.*;
import java.text.ParseException;
import groovy.json.JsonOutput;

class Task implements GroovyInterceptable {


    public static class DateUtils {

        public static def getCalendar(date) {
            def k = Calendar.getInstance();
            k.setTime(date);
            return k;
        }


        static def dateSet(enteredDate, type, value)
        {
            def k = getCalendar(enteredDate);
            k.set(type, value);
            return k.getTime();
        }

        public static def setAll(date, values) {
            values.each {
                date = dateSet(date, it, Calendar.getInstance().get(it));
            }
            return date;
        }

        public static def parse(value) {

            def date = new Date();
            boolean isDateSet = false;
            dateFormats.each {

                if(isDateSet) return;

                try {
                    date = Date.parse(it.key, value);
                    date = setAll(date, it.value);
                    isDateSet = true;
                }
                catch(ParseException parseException) {}

            }

            return date;
        }

        public static def dateFormats = [
            "yyyy-MM-dd HH:mm":[],
            "yyyy-MM-dd HH":[],
            "yyyy-MM-dd":[],
            "MM-dd HH": [YEAR],
            "MM-dd": [YEAR],
            "HH:mm":[YEAR,  MONTH, DATE],
            "dd HH:mm":[YEAR, MONTH],
            "dd HH":[YEAR, MONTH],
            "mm":[YEAR, MONTH, DATE, HOUR]
        ];

    };

    public int id;
    public static int idCount = 0;
    private def properties = [:];
    public Task(int id) {
        this.id = id;
    }

    public def is(k) {
        call(k);
    }

    private def wrapString(s, indent) {
        if(s.indexOf("\n") >= 0) {

            def lines = "\"\"\"$s\"\"\"".split("\n");
            s = lines[0] + "\n";
            for(int i = 1; i < lines.length; i++) {
                s += indent + lines[i] + "\n";
            }
            return s.trim();
        } else {
            return "\"$s\"";
        }
    }

    public def wrapDate(s) {
        return "at('" + s.format("yyyy-MM-dd HH:mm") + "')";
    }

    public def wrap(value, allProps, indent) {
        return value instanceof CharSequence ? wrapString(value, indent) : value instanceof Date ? wrapDate(value) : value instanceof Map ? "[" + value.collect {n -> n.key + ": " + wrap(n.value, allProps, indent) }.join(", ") + "]" : value instanceof List ? "[" + value.collect { n -> wrap(n, allProps, indent) }.join(", ") + "]" : value.toString();
    }

    public String getSpec(indent) {
        def allProps = [:];
        properties.each { allProps[it.key] = wrap(it.value, allProps, indent); }
        allProps["id"] = id;
        def description =  allProps["description"]+ ", ";
        if(description == null) {
            description = ""
        }
        allProps.remove("description")
            allProps.remove("type")
            return description + allProps.toString().replaceAll(/^\[|\]$/, "");
    }

    public Object getProperty(String name) {
        if(name == "id") return id;
        return properties[name];
    }

    @Override
        public void setProperty(String name, Object value) {
            if(name == "type") {
                if(value == null) return;
                if(!(value.toString().toLowerCase() in ["point", "thought", "idea"])) {
                    properties[name] = "thought";
                    return;
                }
            }
            properties[name] = value;
        }

    public String toString() {
        return getDsl("");
    }


    public def taskList = [];
    public def task(...properties) {
        def t = new Task(idCount++);
        properties.each { 
            eachProperty -> 
                if(eachProperty instanceof Closure) {

                    t.processTasks(eachProperty);
                    return;
                }

                else if(eachProperty instanceof CharSequence) {
                    t.setProperty('description', eachProperty);
                }
                else {
                    eachProperty.each { t.setProperty(it.key, it.value) }
                }
        }
        taskList.add(t);
        if(t.init != null) {
            def script = Eval.me(t.init);
            script.delegate = t;
            script.resolveStrategy = Closure.DELEGATE_FIRST;
            script.call();
        }
        return t;
    }

    public def t(...p) { 
        return task(p);
    }

    public def point(...p) {
        def subTask = task(p);
        subTask.setProperty("type", "point")
            return subTask
    }

    public def thought(...p) {
        def subTask = task(p);
        subTask.setProperty("type", "thought")
            return subTask
    }

    public def idea(...p) {
        def subTask = task(p);
        subTask.setProperty("type", "idea")
            return subTask
    }

    public def points(...properties) {
        def lines = ""
            def allProperties = [:];
        properties.each {
            if(it instanceof CharSequence) {
                lines = it.trim();
            } else if(it instanceof Map) {
                it.each { allProperties[it.key] = it.value }
            }
        }
        lines.split('\n').each {
            def eachTask = allProperties.clone();
            eachTask["description"] = it.trim()
                eachTask["type"] = "point";
            task eachTask
        }
    }

    public static final def EXCLUDE_PROPERTY_NODES_FROM_GRAPH = ["description", "id", "connectTo", "color", "type"];

    public def toSigmaStructure(nodes, edges, level) {
        def node = [id:id, x:Math.random(), y: Math.random(), label:description, size:2];
        if(this.color != null) {
            node.color = this.color;
        }
        nodes.add(node);
        if(this.connectTo != null) {
            edges.add([id: id + 0.0000001, source: id, target: connectTo]);
        }
        def p = id + 1;
        def idProp = id + 0.001;
        properties.each {
            if(it.key in EXCLUDE_PROPERTY_NODES_FROM_GRAPH) return;
            nodes.add([id:idProp + "_", x:Math.random(), y:Math.random(), label:"${it.key}=${it.value}", size:1, color:"#10DD92"]);
            edges.add([id:idProp + "_", source:id, target: idProp + "_", color:"#10DD92"]);
            p++;
            idProp += 0.001;
        }
        taskList.each {
            it.toSigmaStructure(nodes, edges, level + 2);
            idProp += 0.001;
            edges.add([id:idProp, source:id, target:it.id]);
        }
    }

    public def toSigmaJson() {
        def nodes =[], edges = [];
        toSigmaStructure(nodes, edges, 1);
        return JsonOutput.toJson([nodes:nodes, edges:edges]);
    }

    public def toPlain() {
        def data = [:];
        properties.each {
            data.put(it.key, it.value);
        }
        data["taskList"] = [];
        taskList.each {
            data["taskList"].add(it.toPlain());
        }
        return data;
    }

    public def toJson() {
        return JsonOutput.toJson(toPlain());
    }

    public def toFlatList(prefix) {
        prefix = (prefix == null ? "" :  prefix);
        def flatList = [];
        flatList.add(prefix + description);
        taskList.each {
            it.toFlatList(prefix + ",").each {
                i -> 
                    flatList.add(i);
            }
        }
        return flatList;
    }

    public def toCsv() {
        def s = "";
        toFlatList(null).each {
            s += it + "\n";
        }
        return s;
    }

    public def getTaskList() {
        return taskList;
    }
    public void processTasks(taskSpecs) {
        taskSpecs.delegate = this;
        taskSpecs.resolveStrategy = Closure.DELEGATE_FIRST;
        taskSpecs.call();
        if(end != null) {
            def script = Eval.me(end);
            script.delegate = this;
            script.resolveStrategy = Closure.DELEGATE_FIRST;
            script.call();
        }
    }

    public def call(k) {
        processTasks(k);
    }

    public static final String INDENT = "  ";

    public def getDsl(indent) {
        def type = properties["type"]
            if(type == null) {
                type = "task";
            }
        def dsl = indent + type + " " + getSpec(indent);

        if(taskList.size() == 0)
            return dsl + "\n";

        dsl += " is {\n";
        taskList.each {
            def taskDsl =  it.getDsl(indent + INDENT);
            dsl += taskDsl;

        }
        dsl += indent + "}\n";
        return dsl;
    }

    public def at(time) {
        return DateUtils.parse(time.toString());
    }

    public def filter(k) {
        def tasks = [];

        try {
            k.resolveStrategy = Closure.DELEGATE_FIRST;
            k.delegate = this;
            if(k.call()) {
                tasks.add(this);
                return tasks;
            }
        } catch(Exception e) {
        }

        taskList.each {
            tasks.addAll(it.filter(k));
        }
        return tasks;
    }
};



static def getChosenFilter() {
    final String COMMON_FILTERS_FILE = 'CommonFilters.groovy';
    def sc = new Scanner(System.in);
    def filters = Eval.me(new File(COMMON_FILTERS_FILE).getText());
    def indexedFilters = [];
    int i = 0;
    filters.each {
        indexedFilters.add(it.value);
        println "${i++} ${it.key}";
    }
    println i + ": Enter Custom Groovy Closure (returns boolean) to filter tasks (example: 'id == 0' will return all items):";
    int choice = Integer.parseInt(sc.nextLine());
    if(choice >= filters.size()) {
        print "Enter filter : { it -> "
            def f = sc.nextLine()
            println "\n}";
        return Eval.me("{ it -> $f }");
    } else {
        return indexedFilters[choice];
    }
}

static def getDsl(t) {
    return "tasks {\n" + t.getDsl(Task.INDENT) + "}\n";
}




static void main(String[] args) {
    final String GRAPH_FILE = System.getenv("TASK_DIRECTORY") + "/graphVisualize/graph.json";
    if(args.length > 1) {
        def file = new File(args[0]);
        def opt = args[1];
        def root = Eval.me("{ it -> return " + file.getText() + "}");
        root.delegate = this;
        root.resolveStrategy = Closure.DELEGATE_FIRST;
        root = root();
        if(opt == 'f') {
            def filter = getChosenFilter();
            def filteredTasks = root.filter(filter);
            def filterGroup = new Task(-2);
            filteredTasks.each { filterGroup.getTaskList().add(it); println it; }
            new File(GRAPH_FILE).text = filterGroup.toSigmaJson()
        } 
        else if(opt == 'o') {
            new File(GRAPH_FILE).text = root.toSigmaJson()
        } else if(opt == "v") {
            def filteredTasks = getFilteredTasks(root);
            def fileName = getOutputFileName();
            filteredTasks.each { 
                new File(fileName) << it.toCsv();
            }

        } else if(opt == "P") {
            def filteredTasks = getFilteredTasks(root);
            def fileName = getOutputFileName();
            def filterGroup = new Task(-2);
            filterGroup.setProperty("id", -2);
            filteredTasks.each { filterGroup.getTaskList().add(it); println it; }
            new File(fileName) << filterGroup.toJson();
        }
        if(root.getTaskList().size() > 0)
            file.text =  getDsl(root.getTaskList()[0]);
        else {
            println "tasks {\n\n}";
        }
    }
}

public static def getOutputFileName() {
    Scanner sc = new Scanner(System.in);
    println "Output File : "
        def fileName = sc.nextLine();
    new File(fileName).write("");
    return fileName;
}

public static def getFilteredTasks(root) {

    def filter = getChosenFilter();
    def filteredTasks = root.filter(filter);
    return filteredTasks;
}

public static def tasks(k) {
    def t = new Task(-1);
    t.processTasks(k);
    return t;
}


