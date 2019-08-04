const unirest = require("unirest");

{% if headers %}const headers = {{headers|safe}};{% endif %}
{% if body %}const body = {{body|safe}};{% endif %}

unirest
  .{{request-method|name}}("{{scheme}}://{{server-name}}{{uri}}")
  {% if headers %}.headers(headers){% endif %}
  {% if query-string %}.query("{{query-string|safe}}"){% endif %}
  {% if body %}.send(body){% endif %}
  .then(({ raw_body, status, code }) => {
    // output response to console as JSON
    console.log(JSON.stringify({ raw_body, status, code }, null, 4));
  });
