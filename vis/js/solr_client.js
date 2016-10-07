import restClient, { SETTINGS } from '../node_modules/es6-rest-client/dist/client.es6';

export default class StremingExprClient {
    constructor(url) {

        this.url = url;

    }

    getGraphData(expr, callback) {
        restClient[SETTINGS] ({
            method: 'POST',
            baseURI: this.url,
            params: {expr: expr}
        });

        restClient().then((response) => {
            callback(response.json())
        })

    }
}