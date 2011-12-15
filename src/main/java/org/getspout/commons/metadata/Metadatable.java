package org.getspout.commons.metadata;

import java.util.List;
import org.getspout.commons.plugin.Plugin;

/**
 * This interface is implemented by all objects that can provide metadata about themselves.
 */
public interface Metadatable {

	/**
	 * Sets a metadata value in the implementing object's metadata store.
	 * @param metadataKey
	 * @param newMetadataValue
	 */
	public void setMetadata(String metadataKey, MetadataValue newMetadataValue);

	/**
	 * Returns a list of previously set metadata values from the implementing object's metadata store.
	 * @param metadataKey
	 * @return A list of values, one for each plugin that has set the requested value.
	 */
	public List<MetadataValue> getMetadata(String metadataKey);

	/**
	 * Tests to see whether the implementing object contains the given metadata value in its metadata store.
	 * @param metadataKey
	 * @return
	 */
	public boolean hasMetadata(String metadataKey);

	/**
	 * Removes the given metadata value from the implementing object's metadata store.
	 * @param metadataKey
	 * @param owningPlugin This plugin's metadata value will be removed. All other values will be left untouched.
	 */
	public void removeMetadata(String metadataKey, Plugin owningPlugin);

}
