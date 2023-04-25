import java.math.BigInteger;
import java.security.SecureRandom;

public class Paillier {
    private final int bitLength;
    private SecureRandom random;
    private BigInteger n, nSquared, g, lambda, mu;

    public Paillier(int bitLength) {
        this.bitLength = bitLength;
        this.random = new SecureRandom();
        generateKeys();
    }

    public Paillier(int bitLength, BigInteger n, BigInteger g, BigInteger lambda, BigInteger mu) {
        this.bitLength = bitLength;
        this.random = new SecureRandom();
        this.n = n;
        this.nSquared = n.multiply(n);
        this.g = g;
        this.lambda = lambda;
        this.mu = mu;
    }

    public BigInteger getN() {
        return this.n;
    }
    public BigInteger getG() {
        return this.g;
    }
    public BigInteger getLambda() {
        return this.lambda;
    }
    public BigInteger getMu() {
        return this.mu;
    }

    public void generateKeys() {
        BigInteger p = BigInteger.probablePrime(bitLength / 2, random);
        BigInteger q = BigInteger.probablePrime(bitLength / 2, random);

        n = p.multiply(q);
        nSquared = n.multiply(n);

        BigInteger pMinusOne = p.subtract(BigInteger.ONE);
        BigInteger qMinusOne = q.subtract(BigInteger.ONE);
        BigInteger phi = pMinusOne.multiply(qMinusOne);

        g = n.add(BigInteger.ONE);
        lambda = phi;
        mu = phi.modInverse(n);
    }

    public BigInteger encrypt(BigInteger plaintext) {
        BigInteger r = BigInteger.probablePrime(bitLength, random);

        BigInteger ciphertext = g.modPow(plaintext, nSquared)
                .multiply(r.modPow(n, nSquared))
                .mod(nSquared);

        return ciphertext;
    }

    public BigInteger decrypt(BigInteger ciphertext) {
        BigInteger plaintext = ciphertext.modPow(lambda, nSquared)
                .subtract(BigInteger.ONE)
                .divide(n)
                .multiply(mu)
                .mod(n);

        return plaintext;
    }

    // public static void main(String[] args) {
    //     Paillier paillier = new Paillier(1024);

    //     String text = "1234567890";
    //     BigInteger plaintext = new BigInteger(text.getBytes());

    //     BigInteger ciphertext = paillier.encrypt(plaintext);

    //     System.out.println("Plaintext: " + new String(plaintext.toByteArray()));
    //     System.out.println("Ciphertext: " + ciphertext);
    //     System.out.println("Decrypted plaintext: " + new String(paillier.decrypt(ciphertext).toByteArray()));
    // }
}