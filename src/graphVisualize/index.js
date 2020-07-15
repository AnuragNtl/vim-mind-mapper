var fs = require("fs");
const GRAPH_FILE = "graph.json";
if(fs.existsSync(GRAPH_FILE)) {
    var express = require("express");
    var app = express();

    app.use(express.static('build'));
    app.get("/", (req, res) => {
        res.sendFile("graph.html", {root:__dirname});
    });

    app.get("/graph.json", (req, res) => {
        res.sendFile(GRAPH_FILE, {root:__dirname});
        server.close();
    })

    var server = app.listen(8083, function(error) {
        if(error) {

            console.log(error);
        }
        console.log("started listening on port 8083.\nNavigate to http://localhost:8083/ to visualize tasks in a graph.");
    });
}

