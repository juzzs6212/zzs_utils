package utils.springmvc;

import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by MyWin on 2017/12/15 0015.
 */
public class ResponseUtil {
    /**
     * 直接输出纯字符串.
     */
    public static void renderJsonObj(HttpServletRequest request, HttpServletResponse response, Object Obj) {
        String text = JSON.toJSONString(Obj);
        response.setContentType("application/json;charset=UTF-8");

        /*判断是否是需要支持JSONP*/
        String callback = request.getParameter("callback");
        if (callback != null && !"".equals(callback)) {
            response.setContentType("application/jsonp;charset=UTF-8");
            text = callback + "(" + text + ")";
        }

        try {
            response.getWriter().write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
