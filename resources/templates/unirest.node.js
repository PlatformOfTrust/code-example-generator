const unirest = require("unirest");

unirest
  .{{request-method|name}}("{{scheme}}://{{server-name}}{{uri}}")
  .headers({{headers|json|safe}})
  .query({{query-string|safe}})
  .send({{body|json|safe}})
  .then(({raw_body, status, code }) => {
    // output response to console as JSON
    console.log(JSON.stringify({ raw_body, status, code }, null, 4));
  })
