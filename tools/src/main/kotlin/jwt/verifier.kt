package jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

fun main(args: Array<String>) {
    val pemPub = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoIpVspMKeL/FZUhGCFGNgD1ASd8qqgSMXzHVECwP95M3JUCPE5xzC03tYtP7VCxZnGN19CxbItpPRxYWXo9aHcvJ4lq7JB0Twhd/K9RETjFiGM9k4AaF2+UGPkyPTcA2nbfix6Hu6dO7SAUA+5rdLKftbSCxhbKu7JoCK+vh1SkGf188V2D8mBYIghg0Xu9mjSAmL5Pzf9GiTGXj5kTAxtxN2XsfBI3z3gccAGIXvw9fLHQSlVt9UQDchduyM59KctAsfWnWHr06hXI+aD295LzvszvO22Fa3RTJsFFHWhC3zqH+eyST7ATF3XWCz8aj6YeEp5p0bJEw096yFDpECQIDAQAB"
    val pemSec = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCgilWykwp4v8VlSEYIUY2APUBJ3yqqBIxfMdUQLA/3kzclQI8TnHMLTe1i0/tULFmcY3X0LFsi2k9HFhZej1ody8niWrskHRPCF38r1EROMWIYz2TgBoXb5QY+TI9NwDadt+LHoe7p07tIBQD7mt0sp+1tILGFsq7smgIr6+HVKQZ/XzxXYPyYFgiCGDRe72aNICYvk/N/0aJMZePmRMDG3E3Zex8EjfPeBxwAYhe/D18sdBKVW31RANyF27Izn0py0Cx9adYevTqFcj5oPb3kvO+zO87bYVrdFMmwUUdaELfOof57JJPsBMXddYLPxqPph4SnmnRskTDT3rIUOkQJAgMBAAECggEBAJTAuTlW38wUvV3kguewUTGzD3+jZg/VWQ/Xx7lNapoW8XD2Cdy9CSg/U6dQDj/nPD0ZN7FA7E02BWEzyRRjNbgIHQ88zMxgmO8uAGrnK0dcoK2mdazSvnOLGabeH6aGZNTtDxe1Jv/IbW8FG+0GiMuY6flE4tikQUsAROTXkxCDlEPtrBvy6BsYHBB58G2pUpqCpk31dJcarla4h2Wzu4mIx/YqK8r2rbDRKuAdRq19s6XGuapbXneqUoeIipnxA0GiF0r7DoYVnjqsYMRoc2A8twImKz/DLaeVPd0EGMlWBqAu6kx2gIAbGMU/ksSnWvotVijfOHeM9vFFdQ2OQlECgYEAza/WPKTDh75B+5idV86HGaTtGpuXBhgTZA6xTUJ/2pDqB6HKaJ19XVeZWJtHPynJmLn5mLmv/mThhcBmO6I2jnG6T3KJWfKEuAqIkcxxzxmBFNXg8/YUq4tg5FonZB7x3yHSRUCe8l2RZOYLspZ8HNGghGx+K7+1HHfWWGoAy00CgYEAx89pxMA03THAJ9+TwX8g0lgKo2V/e1z/ANVD/GKjy96neg3pcIOCsk8SmXbRlxjTsVzA0m0KaM53NfLCv9+MmuzH76EbZ/hq08iNH+JiQhGFsT5Nn9rQMD7ZtYMlRbEOZZOOk23F/zgJ1q0wlsrAAkh4bNkk0b37bgf+oJ6p5a0CgYBVgHGzNd6eD+XH25IUjSK180wo0mK+QsXUwxLLBgi156WRVJ0aTdVuNtrk7W95fzpxRWXPKelR+8eP3QHiajT5k6caHYehH2IySxaoXO82Yui4q4veff3rrX2yjxNu2Do9QgbxiMJKB8FHyyUzsMCZDj8GrT7pmsCDvAEbMb1wxQKBgCPjOhEPoK8V73h57LT1W3TMQWTlpwcGRfrlUyeyo87LN/NxKjt+M2xJTYLo1243FFan3S4uTY9Tfg/fgWErCrsMykBdBGohMTbGr6pJY/KP6R5TnWeXyorPzxSN8JTa30YZQaNzNPvJGeNIChEba289ivup6LYPVGiH4RECTmulAoGAEVMOsf/LwxOPNYhtFU8eE8h+PvHISaCda0zwVuWxU505KjDdRUj/4HAL7NM8pq6NNSNpYMyN8FOv+vyh5c6V/DQF5BsPTH3UoMOJNhoI1gXt+12HLYOYAWpVliPPNr6+q5NcTe+KHb8iv5EyVYN96wAmG41isCnR4YmBkypNLCU="
    val kf = KeyFactory.getInstance("RSA")
    val keyspec1 = X509EncodedKeySpec(Base64.getMimeDecoder().decode(pemPub))
    val publicKey = kf.generatePublic(keyspec1) as RSAPublicKey
    val keyspec2 = PKCS8EncodedKeySpec(Base64.getMimeDecoder().decode(pemSec))
    val privateKey = kf.generatePrivate(keyspec2) as RSAPrivateKey
    val token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJ6aGFuZ3NhbiIsInNjb3BlIjpbXSwiaXNzIjoiZWt1YWliYW8iLCJhZ2UiOiIyMiJ9.qIkLxzCazd30KcbC_I2RhjbLeBkSzJeqUlSLAHUZgN1128Ppo7wWNQXMVcXMm_SE8kk8f8XhrEJgUchw6KbKstt0wbistrgaPMFnX_92GIdTETb5l4Ag2wOvyEb_3NTZoIrmgRlODv1ZdlGOomcEpBxU3PZHnzTc-1AojWzPAHXGdWREmZGgvOiQjpDz0dcsohRaYzFbgqpf60BmQhjYCC0xCeOCi1xcIKi2NWCLJcMxZEjTQakQtEB3-PyL49maRBQfQngikHPLawSxCF75XEVuGzIDiK4sORWqG2CrCOMpwrjz3AOcQXSiGUHA5pI_guxKmNNfIrKDc7HBkR4lHA"
    try {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        val keyPair = keyPairGenerator.generateKeyPair()
        val publicKey2 = keyPair.public as RSAPublicKey
        val privateKey2 = keyPair.private as RSAPrivateKey

        val algorithm2 = Algorithm.RSA256(publicKey, privateKey)
        val verifier = JWT.require(algorithm2)
                .build()
        var jwt = verifier.verify(token)
    } catch (exception: JWTVerificationException) {
        //Invalid signature/claims
        println(exception)
    }
}