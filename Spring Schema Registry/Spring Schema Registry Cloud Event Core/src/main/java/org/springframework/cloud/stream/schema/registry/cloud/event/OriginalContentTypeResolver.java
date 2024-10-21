package org.springframework.cloud.stream.schema.registry.cloud.event;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.ContentTypeResolver;
import org.springframework.util.MimeType;

class OriginalContentTypeResolver implements ContentTypeResolver {

	private static final String BINDER_ORIGINAL_CONTENT_TYPE = "originalContentType";

	private ConcurrentMap<String, MimeType> mimeTypeCache = new ConcurrentHashMap<>();

	@Override
	public MimeType resolve(MessageHeaders headers) {
		Object contentType = headers
				.get(BINDER_ORIGINAL_CONTENT_TYPE) != null
						? headers.get(BINDER_ORIGINAL_CONTENT_TYPE)
						: headers.get(MessageHeaders.CONTENT_TYPE);
		MimeType mimeType = null;
		if (contentType instanceof MimeType mimeContentType) {
			mimeType = mimeContentType;
		}
		else if (contentType instanceof String valueAsString) {
			mimeType = this.mimeTypeCache.get(contentType);
			if (mimeType == null) {
				mimeType = MimeType.valueOf(valueAsString);
				this.mimeTypeCache.put(valueAsString, mimeType);
			}
		}
		return mimeType;
	}

}
