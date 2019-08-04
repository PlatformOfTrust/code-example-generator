const unirest = require("unirest");

unirest
  .{{request-method|name}}("{{scheme}}://{{server-name}}{{uri}}")
  {% if headers %}.headers({{headers|safe}}){% endif %}
  {% if query-string %}.query("{{query-string|safe}}"){% endif %}
  {% if body %}.send({{body|safe}}){% endif %}
  .then(({raw_body, status, code }) => {
    // output response to console as JSON
    console.log(JSON.stringify({ raw_body, status, code }, null, 4));
  })
