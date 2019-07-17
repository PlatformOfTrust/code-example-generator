```python
{% include "templates/python.py" %}
```

```shell
{% include "templates/curl" %}
```

```javascript
{% include "templates/unirest.node.js" %}
```

```java
System.out.println("Java example missing. Why not contribute one for us?");
```

> The above example should return `JSON` structured like this:

```json
The above example should return JSON structured like this:

HTTP/1.0 {{ok.status|name}}

{{ok.body|safe}}

```
