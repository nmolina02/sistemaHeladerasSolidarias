package enviadoresNotificaciones;

import config.EnvConfig;
import okhttp3.*;

import java.io.IOException;

public class EnviadorWhatsapp {
    private static EnviadorWhatsapp instancia = null;

    private EnviadorWhatsapp() {}

    public static EnviadorWhatsapp getInstance() {
        if(instancia == null)
            instancia = new EnviadorWhatsapp();
        return instancia;
    }

    public void enviar_whatsapp(String phoneNumber, String message) {
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("token", EnvConfig.get("WHATSAPP_TOKEN"))
                    .add("to", phoneNumber)
                    .add("body", message)
                    .build();

            Request request = new Request.Builder()
                    .url("https://api.ultramsg.com/" + EnvConfig.get("WHATSAPP_INSTANCE") + "/messages/chat")
                    .post(body)
                    .addHeader("content-type", "application/x-www-form-urlencoded")
                    .build();

            Response response = client.newCall(request).execute();

            System.out.println(response.body().string());

        } catch (IOException e) {
            System.err.println("Error while sending message to " + phoneNumber + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
