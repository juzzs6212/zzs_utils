package utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by MyWin on 2018/7/4 0004.
 */
public class JwtToken {
    /**
     * token秘钥，请勿泄露，请勿随便修改 backups:JKKLJOoasdlfj
     */
    public static final String SECRET = "#<JwtToken>#$";
    public static final String PRO_NAME_UID = "_uid";
    public static final String PRO_NAME_UTYPE = "_utype";
    /**
     * token 过期时间: 10天
     */
    public static final int calendarField = Calendar.HOUR;

    /**
     * @param uid
     * @param utype
     * @return
     * @throws Exception
     */
    public static String createToken(Integer uid, Integer utype, Integer aliveHours) throws Exception {
        Map<String, Object> claimMap = new HashMap<>();
        claimMap.put(PRO_NAME_UID, uid);
        claimMap.put(PRO_NAME_UTYPE, utype);
        return createToken(claimMap, aliveHours);
    }

    /**
     * JWT生成Token.<br/>
     * <p>
     * JWT构成: header, payload, signature
     */
    public static String createToken(Map<String, Object> claimMap, Integer aliveHours) throws Exception {
        Date iatDate = new Date();
        // expire time
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(calendarField, aliveHours);
        Date expiresDate = nowTime.getTime();

        // header Map
        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");

        // build token
        // param backups {iss:Service, aud:APP}
        JWTCreator.Builder builder = JWT.create().withHeader(map) // header
                .withClaim("iss", "Service") // payload
                .withClaim("aud", "APP")
                .withIssuedAt(iatDate) // sign time
                .withExpiresAt(expiresDate); // expire time
        Set<String> keySet = claimMap.keySet();
        for (String key : keySet) {
            builder.withClaim(key, claimMap.get(key).toString());
        }
        //.withClaim("user_id", null == user_id ? null : user_id.toString())
        String token = builder.sign(Algorithm.HMAC256(SECRET)); // signature

        return token;
    }

    /**
     * 解密Token
     *
     * @param token
     * @return
     * @throws Exception
     */
    public static Map<String, Claim> verifyToken(String token) {
        Map<String, Claim> map = null;
        DecodedJWT jwt = null;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
            jwt = verifier.verify(token);
            map = jwt.getClaims();
        } catch (Exception e) {
            // e.printStackTrace();
            // token 校验失败, 抛出Token验证非法异常
        }
        return map;
    }

    /**
     * 根据Token获取user_id
     *
     * @param token
     * @return user_id
     */
    public static TokenIdent getUserIdent(String token) {
        Map<String, Claim> claims = verifyToken(token);
        if (null == claims)
            return null;
        Claim user_id_claim = claims.get(PRO_NAME_UID);
        Claim user_type_claim = claims.get(PRO_NAME_UTYPE);
        if (null == user_id_claim || StringUtils.isEmpty(user_id_claim.asString())
                || null == user_type_claim || StringUtils.isEmpty(user_type_claim.asString())) {
            return null;
        } else {
            return new TokenIdent(Integer.valueOf(user_id_claim.asString()), Integer.valueOf(user_type_claim.asString()));
        }
    }
}
