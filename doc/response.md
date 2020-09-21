
## Response内容定制

除了在@RestController中使用常规方式,如HttpServletResponse参数进行内容设置外, 还可使用如下的方式进行返回.

### 使用 Response对象.

可以使用Response对象对header,statusCode,body内容设置;

```java
import com.bpfaas.common.web.Response;

@RestController
public class DemoController {
    @PostMapping(path = "/demo")
    public Response demo()
    {
        return new Response(){{
            setStatusCode(200);
            setHeader("X-Custom", "value");
            setBody("string content or any object");
        }};
    }
}
```

使用此例, 将会返回如下的内容给调用者:

```bash
"string content or any object"
```


### 使用 Msg对象.

Msg对象对应系统的数据包格式, 可直接返回.

controller示例

```java
import com.bpfaas.common.web.Msg;

@RestController
public class DemoController {
    @PostMapping(path = "/demo")
    public Msg<Object> demo()
    {
        return new Msg<>(){{
            setData("r1", 1);
        }};
    }
}
```

此例将会返回如下的json内容给调用者:

```json
{
    "err": "OK",
    "err_code": 200,
    "data": {
        "r1": 1
    }
}
```

- Msg是一个模板, 模板类型表示 data数据段;
- 提供一个`MsgObject`类, 表示 Msg<Object\>


### 使用 自定义bean.

返回自定义bean, 系统会自动将bean构造到`Msg.data`字段中

controller示例

```java
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 此对象将会作为返回包中data的内容.
 */
@Data
public class DemoBean {
    @JsonProperty("r1")
    private int r1;
}

@RestController
public class DemoController {
    @PostMapping(path = "/demo")
    public DemoBean demo()
    {
        return new DemoBean(){{
            setR1(1);
        }};
    }
}
```

使用此自定义的bean, 将会返回如下的json消息给调用者:

```json
{
    "err": "OK",
    "err_code": 200,
    "data": {
        "r1": 1
    }
}
```
