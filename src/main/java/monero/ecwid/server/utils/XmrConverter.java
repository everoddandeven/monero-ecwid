package monero.ecwid.server.utils;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

import java.math.RoundingMode;


public class XmrConverter {

    private static final String KRAKEN_API_URL = "https://api.kraken.com/0/public/Ticker?pair=XMRUSD";
    private static final BigInteger PICO_MULTIPLIER = BigInteger.valueOf(1_000_000_000_000L); // 10^12

    public static BigDecimal getExchangeRate() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(KRAKEN_API_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Errore nella richiesta HTTP: " + response.statusCode());
        }

        JSONObject jsonResponse = new JSONObject(response.body());
        JSONObject result = jsonResponse.getJSONObject("result");
        String pair = result.keys().next();
        JSONObject pairData = result.getJSONObject(pair);

        BigDecimal bid = new BigDecimal(pairData.getJSONArray("b").getString(0));
        BigDecimal ask = new BigDecimal(pairData.getJSONArray("a").getString(0));
        return bid.add(ask).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
    }

    public static BigInteger convertUsdToPiconero(double usdAmount) throws Exception {
        BigDecimal exchangeRate = getExchangeRate(); // Tasso di cambio XMR/USD
        BigDecimal xmrAmount = BigDecimal.valueOf(usdAmount).divide(exchangeRate, 12, RoundingMode.HALF_UP);
        return xmrAmount.multiply(new BigDecimal(PICO_MULTIPLIER)).toBigInteger();
    }

}
