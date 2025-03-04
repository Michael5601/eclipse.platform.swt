package org.eclipse.swt.graphics;

/**
 * Represents an element, such as some image data, at a specific zoom level.
 *
 * @param <T> type of the element to be presented, e.g., {@link ImageData}
 * @since 3.130
 */
public record ElementAtZoom<T>(T element, int zoom) {
}