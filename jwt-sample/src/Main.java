import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

public class Main {

    public static void main(String[] args) {

        String jwtString = "eyJraWQiOiJtdkpxY3pnSG5rZlFxUkdQUnRUajBEZHhTWVNOV3R6cWlrTUFSX3l5Rjk4IiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiIwMHVmc2xpYW9lYVN5M2kwbzBoNyIsIm5hbWUiOiJfX0ZpcnN0TmFtZV9fIF9fTGFzdE5hbWVfXyIsImxvY2FsZSI6ImVuLVVTIiwidmVyIjoxLCJpc3MiOiJodHRwczovL2Ftd2F5Y29ubmVjdC10ZXN0Lm9rdGFwcmV2aWV3LmNvbSIsImF1ZCI6IjBvYWRjaDk3cTRQSmpyYTRrMGg3IiwiaWF0IjoxNTM2NzU2ODI5LCJleHAiOjE1MzY3NjA0MjksImp0aSI6IklELkRlOGxfN1VFLTJpUVVIZl85ck5NQWpzbHl6MkQ2ajR0bjZwSkxCbjd0T3ciLCJhbXIiOlsicHdkIl0sImlkcCI6IjAwbzlrMjY0YjYyb1N6c0Q2MGg3Iiwibm9uY2UiOiJuLTBTNl9XekEyTWoiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhbWVyaWNhc3FhKzg0MjM2NTRAZ21haWwuY29tIiwiZ2l2ZW5fbmFtZSI6Il9fRmlyc3ROYW1lX18iLCJmYW1pbHlfbmFtZSI6Il9fTGFzdE5hbWVfXyIsInpvbmVpbmZvIjoiQW1lcmljYS9Mb3NfQW5nZWxlcyIsImF1dGhfdGltZSI6MTUzNjc1Njc5OSwiZW1haWxTdGF0dXMiOiJlbmFibGVkIiwibGVnYWxDb3VudHJ5IjoiVVMiLCJwYXJ0eUlkIjoiODQyMzY1NCJ9.PKA0CAxp91bg31HtDLg7PrpdQN-dJ2gsJ_hN6FigLisRvNBM2cwTRuMlsf2YcdxAtEHUJXQ-qXYAm1RiMzFEvFmzZGgBVgQefaX38J9pa5-UQ0oElTJJAt1p4BleXefoKHzeDqzS_lomrkcBCqHJrFQ72OJJ5IhRalH6laBujVuY3CErZzr5B7aekn3gKMRxu1rf4ZO7asLCfnzeuHlgLUDzgNLWcEtVlPWPkFMqhpe_vvovLnj41x1_zVwg34y6AQbO1Z3D891f17JGBv_tX0Ia--3f-m78bdPbg3jAllzgP-kcT7-r0v_0mImeUVJCFjVgcNqhdPdl5kzTosbt9A";

        try {
            SignedJWT signedJWT = SignedJWT.parse(jwtString);

            JWKSet publicKeys = JWKSet.load(new URL("https://amwayconnect-test.oktapreview.com/oauth2/v1/keys"));

            JWK jwk = publicKeys.getKeyByKeyId(signedJWT.getHeader().getKeyID());

            JWSVerifier verifier = new RSASSAVerifier((RSAKey) jwk);

            boolean hasValidSignature = signedJWT.verify(verifier);

            System.out.println(hasValidSignature);

        } catch (JOSEException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
