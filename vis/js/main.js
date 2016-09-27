import SongLikesClient from "./solr_client";


document.addEventListener('DOMContentLoaded', () => {

        let cytoscape = require('cytoscape');
        let cy = window.cy = cytoscape({
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
    let client = new SongLikesClient('http://localhost:8983/solr/social_users_flat/stream');
    let expr = document.getElementById('expr').value;

    cy.remove(cy.elements("node"));
    client.getGraphData(expr,
        (response) => response.then(
            (result) => {
                var nodes = result['result-set'].docs.filter(node => typeof node.node != 'undefined');

                var nodesData = nodes.map(node => {
                    return {
                        data: {
                            id: node.node.indexOf("/") !== -1 ? node.node : node.collection + "/" + node.node,
                            name: node.node,
                            color: node.field == 'email' ? 'black' : node.field == 'node' ? 'yellow' : 'red'
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
                    name: 'circle'
                });

            })
    );
}