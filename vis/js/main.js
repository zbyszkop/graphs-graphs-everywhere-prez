import SongLikesClient from "./solr_client";


document.addEventListener('DOMContentLoaded', () => {

        const cytoscape = require('cytoscape');
        const cy = window.cy = cytoscape({
            container: document.getElementById('cy'),
            style: [
                {
                    selector: 'node',
                    style: {
                        label: 'data(name)',
                        'background-color': 'data(color)'
                    }
                }],
            layout: {
                name: 'circle'
            }

        });
        document.getElementById('new-graph-button').addEventListener('click', requestNewGraph);
    }
);


function requestNewGraph() {
    let expr = document.getElementById('expr').value;
    let path = document.getElementById('path').value;
    let client = new SongLikesClient(path);

    cy.remove(cy.elements("node"));
    client.getGraphData(expr,
        (response) => response.then(
            (result) => {
                const nodes = result['result-set'].docs.filter(node => typeof node.node != 'undefined');

                var nodesData = nodes.map(node => {
                    return {
                        data: {
                            id: node.node.indexOf("/") !== -1 ? node.node : node.collection + "/" + node.node,
                            name: node.node,
                            color: strToRGB(node.field)
                        }
                    };
                });
                var edgesData = nodes.map(node => {
                    let nodeData = [];
                    if (typeof node.ancestors != 'undefined')
                        node.ancestors.forEach(ancestor => {
                            nodeData.push({
                                    data: {
                                        source: ancestor.indexOf("/") !== -1 ? ancestor: node.collection + "/" + ancestor,
                                        target: node.node.indexOf("/") !== -1 ? node.node : node.collection + "/" + node.node
                                    }
                                }
                            )
                        });
                    return nodeData;

                });

                nodesData.forEach(node => cy.add(node));
                edgesData.forEach(nodeData => nodeData.forEach(edge => cy.add(edge)));
                cy.layout({
                    name: 'breadthfirst'
                });
                cy.fit();
            })
    );
}

function hashCode(str) {
    var hash = 0;
    for (var i = 0; i < str.length; i++) {
        hash = str.charCodeAt(i) + ((hash << 5) - hash);
    }
    return hash;
}

function strToRGB(str){
    var i = hashCode(str);
    var c = (i & 0x00FFFFFF)
        .toString(16)
        .toUpperCase();

    return "#" + "00000".substring(0, 6 - c.length) + c;
}