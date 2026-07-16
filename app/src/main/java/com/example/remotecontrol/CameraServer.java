package com.example.remotecontrol;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;

public class CameraServer {
    private static final String TAG = "CameraServer";
    private static final int PORT = 8888;
    private HttpServer server;
    private Context context;
    private Camera camera;
    private String localIP;

    public CameraServer(Context context) {
        this.context = context;
        this.localIP = getLocalIPAddress();
    }

    public void startServer() throws IOException {
        if (server != null) {
            return;
        }

        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new RootHandler());
        server.createContext("/api/control", new ControlHandler());
        server.setExecutor(null);
        server.start();
        Log.d(TAG, "Server started on " + getServerURL());
    }

    public void stopServer() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
        releaseCamera();
    }

    public String getServerURL() {
        return "http://" + localIP + ":" + PORT;
    }

    private String getLocalIPAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface iface : Collections.list(interfaces)) {
                Enumeration<java.net.InetAddress> addresses = iface.getInetAddresses();
                for (java.net.InetAddress addr : Collections.list(addresses)) {
                    if (!addr.isLoopbackAddress() && addr.getHostAddress().contains(".")) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting IP", e);
        }
        return "192.168.1.100";
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = getControlHTML();
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            exchange.sendResponseHeaders(200, html.getBytes(StandardCharsets.UTF_8).length);
            OutputStream os = exchange.getResponseBody();
            os.write(html.getBytes(StandardCharsets.UTF_8));
            os.close();
        }
    }

    class ControlHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if ("GET".equals(method)) {
                String query = exchange.getRequestURI().getQuery();
                if (query != null && query.contains("action=")) {
                    String action = query.split("action=")[1].split("&")[0];
                    handleAction(action);
                }
            }

            String response = "{\"status\":\"ok\"}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    private void handleAction(String action) {
        Log.d(TAG, "Action received: " + action);
        // Actions can be: take_photo, enable_flash, etc.
    }

    private String getControlHTML() {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Remote Camera Control</title>\n" +
                "    <style>\n" +
                "        body { font-family: Arial; max-width: 800px; margin: 0 auto; padding: 20px; background: #f5f5f5; }\n" +
                "        .container { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }\n" +
                "        h1 { color: #333; text-align: center; }\n" +
                "        .button-group { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; margin: 20px 0; }\n" +
                "        button { padding: 15px; font-size: 16px; border: none; border-radius: 4px; cursor: pointer; transition: background 0.3s; }\n" +
                "        .btn-primary { background: #3F51B5; color: white; }\n" +
                "        .btn-primary:hover { background: #303F9F; }\n" +
                "        .btn-danger { background: #f44336; color: white; }\n" +
                "        .btn-danger:hover { background: #da190b; }\n" +
                "        .status { background: #e3f2fd; padding: 15px; border-radius: 4px; margin: 20px 0; }\n" +
                "        .info { background: #fff3e0; padding: 15px; border-radius: 4px; margin: 20px 0; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <h1>📱 Remote Camera Control</h1>\n" +
                "        <div class=\"info\">\n" +
                "            <p><strong>Status:</strong> Connected ✓</p>\n" +
                "            <p><strong>Phone A (This Device):</strong> Streaming camera feed</p>\n" +
                "            <p><strong>Phone B:</strong> Use buttons below to control</p>\n" +
                "        </div>\n" +
                "        <div class=\"button-group\">\n" +
                "            <button class=\"btn-primary\" onclick=\"takePhoto()\">📷 Take Photo</button>\n" +
                "            <button class=\"btn-primary\" onclick=\"toggleFlash()\">⚡ Toggle Flash</button>\n" +
                "            <button class=\"btn-danger\" onclick=\"zoomIn()\">🔍 Zoom In</button>\n" +
                "            <button class=\"btn-danger\" onclick=\"zoomOut()\">🔍 Zoom Out</button>\n" +
                "        </div>\n" +
                "        <div class=\"status\" id=\"response\">Ready for commands...</div>\n" +
                "    </div>\n" +
                "    <script>\n" +
                "        function sendCommand(action) {\n" +
                "            fetch('/api/control?action=' + action)\n" +
                "                .then(r => r.json())\n" +
                "                .then(d => {\n" +
                "                    document.getElementById('response').innerHTML = 'Command sent: ' + action + ' ✓';\n" +
                "                })\n" +
                "                .catch(e => {\n" +
                "                    document.getElementById('response').innerHTML = 'Error: ' + e.message;\n" +
                "                });\n" +
                "        }\n" +
                "        function takePhoto() { sendCommand('take_photo'); }\n" +
                "        function toggleFlash() { sendCommand('toggle_flash'); }\n" +
                "        function zoomIn() { sendCommand('zoom_in'); }\n" +
                "        function zoomOut() { sendCommand('zoom_out'); }\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }
}
