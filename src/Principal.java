import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import com.google.gson.*;

public class Principal {
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/3f32c40b8a22edaeb5425871/latest/USD"; // Cambia la URL si es necesario

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HttpClient client = HttpClient.newHttpClient();
        boolean continuar = true;

        while (continuar) {
            try {
                // Realizar la solicitud HTTP para obtener las tasas de cambio
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(API_URL))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Verificar el código de estado de la respuesta
                if (response.statusCode() == 200) {
                    // Parsear la respuesta JSON
                    Gson gson = new Gson();
                    JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);

                    // Verificar si la respuesta tiene éxito
                    if (jsonResponse.get("result").getAsString().equals("success")) {
                        JsonObject rates = jsonResponse.getAsJsonObject("conversion_rates");

                        // Monedas para filtrar
                        String[] currenciesToFilter = {"COP", "BRL", "USD", "EUR"};

                        // Mostrar las opciones de conversión disponibles
                        System.out.println("\n********************************");
                        System.out.println("****Sea bienvenido/a al Conversor de monedas****");
                        System.out.println("********************************");
                        System.out.println("Opciones de conversión disponibles:");

                        for (int i = 0; i < currenciesToFilter.length; i++) {
                            if (rates.has(currenciesToFilter[i])) {
                                System.out.println((i + 1) + ") " + obtenerNombreMoneda("USD") + " a " + obtenerNombreMoneda(currenciesToFilter[i]));
                            } else {
                                System.out.println("No hay datos disponibles para " + currenciesToFilter[i]);
                            }
                        }

                        // Mostrar las opciones adicionales
                        System.out.println((currenciesToFilter.length + 1) + ") " + obtenerNombreMoneda("EUR") + " a " + obtenerNombreMoneda("USD"));
                        System.out.println((currenciesToFilter.length + 2) + ") " + obtenerNombreMoneda("COP") + " a " + obtenerNombreMoneda("BRL"));
                        // Mostrar la opción para salir
                        System.out.println((currenciesToFilter.length + 3) + ") Salir");

                        // Solicitar al usuario la entrada
                        System.out.print("Elija una opción del menú: ");
                        int opcion = scanner.nextInt();
                        if (opcion >= 1 && opcion <= currenciesToFilter.length) {
                            System.out.print("Ingrese el valor en " + obtenerNombreMoneda("USD") + " que desea convertir: ");
                            double amount = scanner.nextDouble();
                            String targetCurrency = currenciesToFilter[opcion - 1]; // Convertir la opción del menú a la moneda correspondiente

                            // Realizar la conversión de moneda
                            double targetRate = rates.get(targetCurrency).getAsDouble();
                            double result = amount * targetRate;

                            // Mostrar el resultado
                            System.out.printf("%.2f %s equivale a %.2f %s%n", amount, obtenerNombreMoneda("USD"), result, obtenerNombreMoneda(targetCurrency));
                        } else if (opcion == currenciesToFilter.length + 1) {
                            // Conversión de EUR a USD
                            System.out.print("Ingrese el valor en " + obtenerNombreMoneda("EUR") + " que desea convertir: ");
                            double amount = scanner.nextDouble();
                            double eurToUsdRate = rates.get("EUR").getAsDouble();
                            double result = amount * eurToUsdRate;
                            System.out.printf("%.2f %s equivale a %.2f %s%n", amount, obtenerNombreMoneda("EUR"), result, obtenerNombreMoneda("USD"));
                        } else if (opcion == currenciesToFilter.length + 2) {
                            // Conversión de COP a BRL
                            System.out.print("Ingrese el valor en " + obtenerNombreMoneda("COP") + " que desea convertir: ");
                            double amount = scanner.nextDouble();
                            double copToBrlRate = rates.get("COP").getAsDouble() / rates.get("BRL").getAsDouble(); // COP a USD y luego USD a BRL
                            double result = amount * copToBrlRate;
                            System.out.printf("%.2f %s equivale a %.2f %s%n", amount, obtenerNombreMoneda("COP"), result, obtenerNombreMoneda("BRL"));
                        } else if (opcion == currenciesToFilter.length + 3) {
                            System.out.println("Saliendo del programa...");
                            continuar = false; // Establecer continuar a false para salir del bucle
                        } else {
                            System.err.println("La opción seleccionada no es válida.");
                        }
                    } else {
                        System.err.println("La solicitud no fue exitosa: " + jsonResponse.get("error"));
                    }
                } else {
                    System.err.println("La solicitud no fue exitosa. Código de estado: " + response.statusCode());
                }

            } catch (Exception e) {
                System.err.println("Error al procesar la solicitud: " + e.getMessage());
            }
        }
        scanner.close();
    }

    // Método para obtener el nombre de la moneda según su código
    private static String obtenerNombreMoneda(String codigoMoneda) {
        switch (codigoMoneda) {
            case "COP":
                return "Peso colombiano";
            case "BRL":
                return "Real brasileño";
            case "USD":
                return "Dólar estadounidense";
            case "EUR":
                return "Euro";
            default:
                return "Moneda Desconocida";
        }
    }
}

