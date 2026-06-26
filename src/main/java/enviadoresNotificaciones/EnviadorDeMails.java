package enviadoresNotificaciones;

import config.EnvConfig;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public class EnviadorDeMails {
    private static final String apiKey = EnvConfig.get("MAILGUN_API_KEY");
    private static final String dominio = EnvConfig.get("MAILGUN_DOMAIN");
    private static EnviadorDeMails instancia = null;

    private EnviadorDeMails() {}

    public static EnviadorDeMails getInstance() {
        if(instancia == null)
            instancia = new EnviadorDeMails();
        return instancia;
    }

    public void enviar_email(String from, String to, String subject, String text) {
        HttpResponse<String> response = Unirest.post("https://api.mailgun.net/v3/" + dominio + "/messages")
                .basicAuth("api", apiKey)
                .field("from", from)
                .field("to", to)
                .field("subject", subject)
                .field("text", text)
                .asString();

        System.out.println(response.getBody());
    }
}
