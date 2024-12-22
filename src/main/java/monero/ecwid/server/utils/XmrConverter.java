package monero.ecwid.server.utils;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

import java.math.RoundingMode;


public abstract class XmrConverter {

    public enum Source {
        Kraken,
        Gateio,
        Kucoin,
        Htx,
        Mexc
    };

    private static final String GATEIO_API_URL = "https://api.gateio.ws/api/v4/spot/tickers?currency_pair=XMR_USDT";
    private static final String KRAKEN_API_URL = "https://api.kraken.com/0/public/Ticker?pair=XMRUSD";
    private static final String KUCOIN_API_URL = "https://api.kucoin.com/api/v1/market/orderbook/level1?symbol=XMR-USDT";
    private static final String HTX_API_URL = "https://api.huobi.pro/market/detail/merged?symbol=xmrusdt";
    private static final String MEXC_API_URL = "https://www.mexc.com/open/api/v2/market/ticker?symbol=XMR_USDT";

    private static final BigInteger PICO_MULTIPLIER = BigInteger.valueOf(1_000_000_000_000L); // 10^12

    private static BigDecimal getMexcExchangeRate() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(MEXC_API_URL))
                .GET()
                .build();
    
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    
        if (response.statusCode() != 200) {
            throw new RuntimeException("Errore nella richiesta HTTP a MXC: " + response.statusCode());
        }
    
        JSONObject jsonResponse = new JSONObject(response.body());
        JSONObject data = jsonResponse.getJSONArray("data").getJSONObject(0);
    
        BigDecimal bid = new BigDecimal(data.getString("bid"));
        BigDecimal ask = new BigDecimal(data.getString("ask"));
        return bid.add(ask).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
    }
    
    private static BigDecimal getHtxExchangeRate() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(HTX_API_URL))
                .GET()
                .build();
    
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    
        if (response.statusCode() != 200) {
            throw new RuntimeException("Errore nella richiesta HTTP a HTX: " + response.statusCode());
        }
    
        JSONObject jsonResponse = new JSONObject(response.body());
        JSONObject tick = jsonResponse.getJSONObject("tick");
    
        BigDecimal bid = new BigDecimal(tick.getJSONArray("bid").getBigDecimal(0).toString());
        BigDecimal ask = new BigDecimal(tick.getJSONArray("ask").getBigDecimal(0).toString());
        return bid.add(ask).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
    }
    
    private static BigDecimal getKucoinExchangeRate() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(KUCOIN_API_URL))
                .GET()
                .build();
    
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    
        if (response.statusCode() != 200) {
            throw new RuntimeException("Errore nella richiesta HTTP a KuCoin: " + response.statusCode());
        }
    
        JSONObject jsonResponse = new JSONObject(response.body());
        JSONObject data = jsonResponse.getJSONObject("data");
    
        BigDecimal bid = new BigDecimal(data.getString("bestBid"));
        BigDecimal ask = new BigDecimal(data.getString("bestAsk"));
        return bid.add(ask).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
    }
    
    private static BigDecimal getGateioExchangeRate() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GATEIO_API_URL))
                .GET()
                .build();
    
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    
        if (response.statusCode() != 200) {
            throw new RuntimeException("Errore nella richiesta HTTP a Gate.io: " + response.statusCode());
        }
    
        JSONObject jsonResponse = new JSONObject(response.body()).getJSONArray("tickers").getJSONObject(0);
    
        BigDecimal bid = new BigDecimal(jsonResponse.getString("lowest_ask"));
        BigDecimal ask = new BigDecimal(jsonResponse.getString("highest_bid"));
        return bid.add(ask).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
    }
    
    private static BigDecimal getKrakenExchangeRate() throws Exception {
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

    public static BigDecimal getExchangeRate() throws Exception {
        return getExchangeRate(Source.Kraken);
    }

    public static BigDecimal getExchangeRate(Source source) throws Exception {
        if (source == Source.Kraken) {
            return getKrakenExchangeRate();
        }
        else if (source == Source.Gateio) {
            return getGateioExchangeRate();
        }
        else if (source == Source.Kucoin) {
            return getKucoinExchangeRate();
        }
        else if (source == Source.Htx) {
            return getHtxExchangeRate();
        }
        else if (source == Source.Mexc) {
            return getMexcExchangeRate();
        }

        throw new Exception("Invalid source provided");
    }

    public static BigInteger convertUsdToPiconero(double usdAmount) throws Exception {
        return convertUsdToPiconero(usdAmount, Source.Kraken);
    }

    public static BigInteger convertUsdToPiconero(double usdAmount, Source source) throws Exception {
        BigDecimal exchangeRate = getExchangeRate(source); // Tasso di cambio XMR/USD
        BigDecimal xmrAmount = BigDecimal.valueOf(usdAmount).divide(exchangeRate, 12, RoundingMode.HALF_UP);
        return xmrAmount.multiply(new BigDecimal(PICO_MULTIPLIER)).toBigInteger();
    }

}
