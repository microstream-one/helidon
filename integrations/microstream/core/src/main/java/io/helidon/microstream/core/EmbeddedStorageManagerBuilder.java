package io.helidon.microstream.core;

import java.util.Map;

import io.helidon.config.Config;
import one.microstream.storage.configuration.Configuration;
import one.microstream.storage.configuration.ConfigurationPropertyParser;
import one.microstream.storage.types.EmbeddedStorageManager;

/**
 * Fluent API builder to create a EmbeddedStorageManager instance.
 */
public class EmbeddedStorageManagerBuilder implements io.helidon.common.Builder<EmbeddedStorageManager> {

	private final Configuration configuration;

	private EmbeddedStorageManagerBuilder() {
		super();
		configuration = Configuration.Default();
	}
	
    /**
     * A builder for the EmbeddedStorageManager.
     *
     * @return a new fluent API builder
     */
	public static EmbeddedStorageManagerBuilder builder() {
		return new EmbeddedStorageManagerBuilder();
	}

    /**
     * 
     * Create a EmbeddedStorageManager instance from Config
     * 
     * @param config configuration to use
     * @return new EmbeddedStorageManager instance
     */
	public static EmbeddedStorageManager create(Config config) {
		return builder().config(config).build();
	}

	@Override
	public EmbeddedStorageManager build() {
		return configuration.createEmbeddedStorageFoundation().createEmbeddedStorageManager();
	}

	/**
	 * Update builder from configuration.
	 * 
	 * @param config
	 * @return the fluent API builder
	 */
	public EmbeddedStorageManagerBuilder config(Config config) {
		Map<String, String> configMap = config.get("microstream").detach().asMap().get();
		ConfigurationPropertyParser.New().parseProperties(configMap, configuration);

		return this;
	}

	public EmbeddedStorageManagerBuilder baseDirectory(String baseDirectory) {
		configuration.setBaseDirectory(baseDirectory);
		return this;
	}
	
	public String baseDirectory() {
		return configuration.getBaseDirectory();
	}
	
	public EmbeddedStorageManagerBuilder backupDirectory(String backupDirectory) {
		configuration.setBackupDirectory(backupDirectory);
		return this;
	}
	
	public String backupDirectory() {
		return configuration.getBackupDirectory();
	}
	
	public EmbeddedStorageManagerBuilder deletionDirectory(String deletionDirectory) {
		configuration.setDeletionDirectory(deletionDirectory);
		return this;
	}
	
	public String deletionDirectory() {
		return configuration.getDeletionDirectory();
	}
	
	public EmbeddedStorageManagerBuilder truncationDirectory(String truncationDirectory) {
		configuration.setDeletionDirectory(truncationDirectory);
		return this;
	}
	
	public String truncationDirectory() {
		return configuration.getTruncationDirectory();
	}
	
	public EmbeddedStorageManagerBuilder channelDirectoryPrefix(String channelDirectoryPrefix) {
		configuration.setChannelDirectoryPrefix(channelDirectoryPrefix);
		return this;
	}
	
	public String channelDirectoryPrefix() {
		return configuration.getChannelDirectoryPrefix();
	}
	
	public EmbeddedStorageManagerBuilder dataFilePrefix(String dataFilePrefix) {
		configuration.setDataFilePrefix(dataFilePrefix);
		return this;
	}
	
	public String dataFilePrefix() {
		return configuration.getDataFilePrefix();
	}
	
	public EmbeddedStorageManagerBuilder dataFileSuffix(String dataFileSuffix) {
		configuration.setDataFileSuffix(dataFileSuffix);
		return this;
	}
	
	public String dataFileSuffix() {
		return configuration.getDataFileSuffix();
	}
	
	public EmbeddedStorageManagerBuilder transactionFilePrefix(String transactionFilePrefix) {
		configuration.setTransactionFilePrefix(transactionFilePrefix);
		return this;
	}
	
	public String transactionFilePrefix() {
		return configuration.getTransactionFilePrefix();
	}
	
	public EmbeddedStorageManagerBuilder transactionFileSuffix(String transactionFileSuffix) {
		configuration.setTransactionFileSuffix(transactionFileSuffix);
		return this;
	}
	
	public String transactionFileSuffix() {
		return configuration.getTransactionFileSuffix();
	}
	
	public EmbeddedStorageManagerBuilder typeDictionaryFilename(String typeDictionaryFilename) {
		configuration.setTypeDictionaryFilename(typeDictionaryFilename);
		return this;
	}
	
	public String typeDictionaryFilename() {
		return configuration.getTypeDictionaryFilename();
	}
	
	public EmbeddedStorageManagerBuilder rescuedFileSuffix(String rescuedFileSuffix) {
		configuration.setRescuedFileSuffix(rescuedFileSuffix);
		return this;
	}
	
	public String rescuedFileSuffix() {
		return configuration.getRescuedFileSuffix();
	}
	
	public EmbeddedStorageManagerBuilder lockFileName(String lockFileName) {
		configuration.setLockFileName(lockFileName);
		return this;
	}
	
	public String lockFileName() {
		return configuration.getLockFileName();
	}
	
	public EmbeddedStorageManagerBuilder channelCount(int channelCount) {
		configuration.setChannelCount(channelCount);
		return this;
	}
	
	public int channelCount() {
		return configuration.getChannelCount();
	}
	
	public EmbeddedStorageManagerBuilder housekeepingIntervalMs(long housekeepingIntervalMs) {
		configuration.setHousekeepingIntervalMs(housekeepingIntervalMs);
		return this;
	}
	
	public long housekeepingIntervalMs() {
		return configuration.getHousekeepingIntervalMs();
	}
	
	public EmbeddedStorageManagerBuilder housekeepingTimeBudgetNs(long housekeepingTimeBudgetNs) {
		configuration.setHousekeepingTimeBudgetNs(housekeepingTimeBudgetNs);
		return this;
	}
	
	public long housekeepingTimeBudgetNs() {
		return configuration.getHousekeepingTimeBudgetNs();
	}
	
	public EmbeddedStorageManagerBuilder entityCacheTimeoutMs(long entityCacheTimeoutMs) {
		configuration.setEntityCacheTimeoutMs(entityCacheTimeoutMs);
		return this;
	}
	
	public long entityCacheTimeoutMs() {
		return configuration.getEntityCacheTimeoutMs();
	}
	
	public EmbeddedStorageManagerBuilder entityCacheThreshold(long entityCacheThreshold) {
		configuration.setEntityCacheThreshold(entityCacheThreshold);
		return this;
	}
	
	public long entityCacheThreshold() {
		return configuration.getEntityCacheThreshold();
	}
	
	public EmbeddedStorageManagerBuilder dataFileMinimumSize(int dataFileMinimumSize) {
		configuration.setDataFileMinimumSize(dataFileMinimumSize);
		return this;
	}
	
	public int dataFileMinimumSize() {
		return configuration.getDataFileMinimumSize();
	}
	
	public EmbeddedStorageManagerBuilder dataFileMaximumSize(int dataFileMaximumSize) {
		configuration.setDataFileMaximumSize(dataFileMaximumSize);
		return this;
	}
	
	public int dataFileMaximumSize() {
		return configuration.getDataFileMaximumSize();
	}
	
	public EmbeddedStorageManagerBuilder dataFileMinimumUseRatio(double dataFileMinimumUseRatio) {
		configuration.setDataFileMinimumUseRatio(dataFileMinimumUseRatio);
		return this;
	}
	
	public double dataFileMinimumUseRatio() {
		return configuration.getDataFileMinimumUseRatio();
	}
	
	public EmbeddedStorageManagerBuilder dataFileCleanupHeadFile(boolean dataFileCleanupHeadFile) {
		configuration.setDataFileCleanupHeadFile(dataFileCleanupHeadFile);
		return this;
	}
	
	public boolean dataFileCleanupHeadFile() {
		return configuration.getDataFileCleanupHeadFile();
	}
}