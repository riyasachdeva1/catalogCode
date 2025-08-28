import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.*;
import java.util.*;
import org.json.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Read JSON from file
            String content = Files.readString(Path.of("testcase1.json")); // Change to testcase2.json as needed
            JSONObject json = new JSONObject(content);

            int n = json.getJSONObject("keys").getInt("n");
            int k = json.getJSONObject("keys").getInt("k");

            List<Point> points = new ArrayList<>();

            // Extract and decode first k points
            for (int i = 1; i <= n; i++) {
                if (!json.has(String.valueOf(i))) continue;

                JSONObject obj = json.getJSONObject(String.valueOf(i));
                int base = Integer.parseInt(obj.getString("base"));
                String valueStr = obj.getString("value");

                BigInteger y = new BigInteger(valueStr, base);
                BigInteger x = BigInteger.valueOf(i);

                points.add(new Point(x, y));
                if (points.size() == k) break;
            }

            BigInteger secret = lagrangeInterpolationAtZero(points);
            System.out.println("Secret (constant term a_0): " + secret);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    // Data class to store points
    static class Point {
        BigInteger x, y;
        Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }

    // Lagrange Interpolation to compute value at x = 0
    static BigInteger lagrangeInterpolationAtZero(List<Point> points) {
        int k = points.size();
        BigDecimal result = BigDecimal.ZERO;

        for (int j = 0; j < k; j++) {
            BigDecimal term = new BigDecimal(points.get(j).y);
            for (int m = 0; m < k; m++) {
                if (m != j) {
                    BigDecimal numerator = new BigDecimal(points.get(m).x).negate();
                    BigDecimal denominator = new BigDecimal(points.get(j).x.subtract(points.get(m).x));
                    term = term.multiply(numerator).divide(denominator, 50, BigDecimal.ROUND_HALF_UP);
                }
            }
            result = result.add(term);
        }

        return result.setScale(0, BigDecimal.ROUND_HALF_UP).toBigInteger();
    }
}
