import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
import java.util.*;

public class CatalogPolynomialAssignment {
    static class Root {
        int x;
        int base;
        String value;
        long yDecoded;
        Root(int x, int base, String value, long yDecoded) {
            this.x = x;
            this.base = base;
            this.value = value;
            this.yDecoded = yDecoded;
        }
    }

    // Decode y-value from its given base
    static long decode(String value, int base) {
        return new java.math.BigInteger(value, base).longValue();
    }

    // Lagrange interpolation to find f(0) (the secret c)
    static double lagrangeSecret(List<Root> roots) {
        int n = roots.size();
        double secret = 0;
        for (int i = 0; i < n; i++) {
            double term = roots.get(i).yDecoded;
            for (int j = 0; j < n; j++) {
                if (i != j)
                    term *= (0.0 - roots.get(j).x) / (roots.get(i).x - roots.get(j).x);
            }
            secret += term;
        }
        return secret;
    }

    public static void main(String[] args) throws Exception {
        String file = "testcase.json"; // Should be in your workspace root
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(new FileReader(file));
        JSONObject keys = (JSONObject) obj.get("keys");
        int k = Integer.parseInt(keys.get("k").toString());

        List<Root> roots = new ArrayList<>();
        for (Object key : obj.keySet()) {
            if (!"keys".equals(key)) {
                JSONObject rootObj = (JSONObject) obj.get(key);
                int x = Integer.parseInt(key.toString());
                int base = Integer.parseInt(rootObj.get("base").toString());
                String value = rootObj.get("value").toString();
                long yDecoded = decode(value, base);
                roots.add(new Root(x, base, value, yDecoded));
            }
        }

        Collections.sort(roots, Comparator.comparingInt(o -> o.x));
        List<Root> selectedRoots = roots.subList(0, k);
        double secret = lagrangeSecret(selectedRoots);

        System.out.println("Decoded Roots:");
        for (Root r : selectedRoots)
            System.out.println("x = " + r.x + ", y = " + r.yDecoded + " (base " + r.base + ")");
        System.out.println("Secret c (f(0)) = " + Math.round(secret));
    }
}
