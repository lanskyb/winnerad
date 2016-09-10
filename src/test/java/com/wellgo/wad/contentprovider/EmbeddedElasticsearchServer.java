package com.wellgo.wad.contentprovider;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.elasticsearch.common.settings.Settings;



import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

public class EmbeddedElasticsearchServer {

    private static final String DEFAULT_DATA_DIRECTORY = "target/elasticsearch-data";

    private final Node node;
    private final String dataDirectory;

    public EmbeddedElasticsearchServer() {
        this(DEFAULT_DATA_DIRECTORY);
    }

    public EmbeddedElasticsearchServer(String dataDirectory) {
        this.dataDirectory = dataDirectory;

        Settings settings = Settings.builder()
        .put("path.home", dataDirectory)
        .put("path.conf", dataDirectory)
        .put("path.data", dataDirectory)
        .put("path.work", dataDirectory)
        .put("path.logs", dataDirectory)
        //.put("http.port", HTTP_PORT)
        //.put("transport.tcp.port", HTTP_TRANSPORT_PORT)
        .put("index.number_of_shards", "1")
        .put("index.number_of_replicas", "0")
        .put("discovery.zen.ping.multicast.enabled", "false")
        .put("cluster.name", "esonaws")
        .build();

        node = nodeBuilder().settings(settings).client(false).node();
        node.start();
    }

    public Client getClient() {
        return node.client();
    }

    public void shutdown() {
    	System.out.println("EmbeddedElasticsearchServer - shutdown >>>");
    	node.client().close();
        node.close();
        deleteDataDirectory();
        System.out.println("EmbeddedElasticsearchServer - shutdown <<<");
    }

    private void deleteDataDirectory() {
        try {
            FileUtils.deleteDirectory(new File(dataDirectory));
        } catch (IOException e) {
            throw new RuntimeException("Could not delete data directory of embedded elasticsearch server", e);
        }
    }
}