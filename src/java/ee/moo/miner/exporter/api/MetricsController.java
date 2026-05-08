/*
   miner-exporter - Prometheus exporter for cryptocurrency miners
   Copyright 2026 Tarmo Lehtpuu

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package ee.moo.miner.exporter.api;

import ee.moo.miner.exporter.miner.Miner;
import ee.moo.miner.exporter.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

import java.nio.ByteBuffer;

import static java.nio.charset.StandardCharsets.UTF_8;

@RequiredArgsConstructor
@Slf4j
public class MetricsController implements Controller {

    private final Miner miner;

    @Override
    public boolean matches(Request request) {
        if (!StringUtil.equals("GET", request.getMethod())) {
            return false;
        }

        return StringUtil.equals("/metrics", request.getHttpURI().getPath());
    }

    @Override
    public void handle(Request request, Response response, Callback callback) {
        var bytes = miner
            .getMetrics()
            .export()
            .getBytes(UTF_8);

        response.setStatus(200);
        response.getHeaders().put("Content-Type", "text/plain; version=0.0.4; charset=utf-8");
        response.write(true, ByteBuffer.wrap(bytes), callback);
    }
}
