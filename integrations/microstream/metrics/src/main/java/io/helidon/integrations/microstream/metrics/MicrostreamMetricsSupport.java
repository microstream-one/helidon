package io.helidon.integrations.microstream.metrics;

import java.util.Objects;

import org.eclipse.microprofile.metrics.Gauge;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.Metric;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.Tag;

import io.helidon.config.Config;
import io.helidon.metrics.MetricsSupport;
import io.helidon.metrics.RegistryFactory;
import one.microstream.storage.types.EmbeddedStorageManager;

public class MicrostreamMetricsSupport {

	private static final String CONFIG_METRIC_ENABLED_VENDOR = "vendor.";
	static final String BASE_ENABLED_KEY = CONFIG_METRIC_ENABLED_VENDOR + "enabled";

    private static final Metadata GLOBAL_FILE_COUNT = Metadata.builder()
            .withName("microstream.globalFileCount")
            .withDisplayName("total storage file count")
            .withDescription("Displays the number of storage files.")
            .withType(MetricType.GAUGE)
            .withUnit(MetricUnits.NONE)
            .build();
    
    private static final Metadata LIVE_DATA_LENGTH = Metadata.builder()
            .withName("microstream.liveDataLength")
            .withDisplayName("live data length")
            .withDescription("Displays live data length. This is the 'real' size of the stored data.")
            .withType(MetricType.GAUGE)
            .withUnit(MetricUnits.BYTES)
            .build();
    
    private static final Metadata TOTAL_DATA_LENGTH = Metadata.builder()
            .withName("microstream.totalDataLength")
            .withDisplayName("total data length")
            .withDescription("Displays total data length. This is the accumulated size of all storage data files.")
            .withType(MetricType.GAUGE)
            .withUnit(MetricUnits.BYTES)
            .build();
        	
    /**
     * Create a new builder to construct an instance.
     *
     * @return A new builder instance
     */
    public static Builder builder(EmbeddedStorageManager embeddedStorageManager) {
        return new Builder(embeddedStorageManager);
    }

    private Config config;
	private EmbeddedStorageManager embeddedStorageManager;
	private RegistryFactory registryFactory;
	private MetricRegistry vendorRegistry;

	private MicrostreamMetricsSupport(Builder builder) {
		super();
		this.config = builder.config();
		this.embeddedStorageManager = builder.embeddedStorageManager();
		this.registryFactory = builder.registryFactory();	
		
		if(registryFactory == null) {
			registryFactory = RegistryFactory.getInstance(config);			
		}
		
		this.vendorRegistry = registryFactory.getRegistry(MetricRegistry.Type.VENDOR);				
	}
    	
	private void register(Metadata meta, Metric metric, Tag... tags) {
        if (config.get(CONFIG_METRIC_ENABLED_VENDOR + meta.getName() + ".enabled")
        	.asBoolean()
        	.orElse(true)) {
        	vendorRegistry.register(meta, metric, tags);
        }
    }
	
	public void registerMetrics() {		
		register(GLOBAL_FILE_COUNT, (Gauge<Long>) () -> embeddedStorageManager.createStorageStatistics().fileCount());
		register(LIVE_DATA_LENGTH, (Gauge<Long>) () -> embeddedStorageManager.createStorageStatistics().liveDataLength());
		register(TOTAL_DATA_LENGTH, (Gauge<Long>) () -> embeddedStorageManager.createStorageStatistics().totalDataLength());		
	}
	
	
	/**
     * A fluent API builder to build instances of {@link MetricsSupport}.
     */
    public static final class Builder implements io.helidon.common.Builder<MicrostreamMetricsSupport> {
    	 
        private EmbeddedStorageManager embeddedStorageManager;
    	private Config config = Config.empty();
    	private RegistryFactory registryFactory;
    	
    	private Builder(EmbeddedStorageManager embeddedStorageManager)
    	{
    		Objects.requireNonNull(embeddedStorageManager);
    		this.embeddedStorageManager = embeddedStorageManager;
    	}
    	
    	@Override
    	public MicrostreamMetricsSupport build() {
    		return new MicrostreamMetricsSupport(this);
    	}
    	
    	public RegistryFactory registryFactory() {
			return this.registryFactory;
		}

		public EmbeddedStorageManager embeddedStorageManager() {			
			return this.embeddedStorageManager;
		}

		public Config config() {			
			return this.config;
		}
    	
    	public Builder registryFactory(RegistryFactory registryFactory)
    	{
    		this.registryFactory = registryFactory;    		
    		return this;
    	}
    	
    	public Builder config(Config config)
    	{
    		this.config = config;    		
    		return this;
    	}
    }	
}
