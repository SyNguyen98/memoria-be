package org.chika.memoria.utils;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.MessageFormat;

public class PaginationUtil {

    private static final String HEADER_X_TOTAL_COUNT = "X-Total-Count";
    private static final String HEADER_LINK_FORMAT = "<{0}>; rel=\"{1}\"";

    private PaginationUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static <T> HttpHeaders generatePaginationHttpHeaders(final UriComponentsBuilder uriBuilder, final Page<T> page) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_X_TOTAL_COUNT, String.valueOf(page.getTotalElements()));
        final int pageNumber = page.getNumber();
        final int pageSize = page.getSize();
        final StringBuilder link = new StringBuilder();
        if (pageNumber < page.getTotalPages() - 1) {
            link.append(prepareLink(uriBuilder, pageNumber + 1, pageSize, "next"))
                    .append(",");
        }
        if (pageNumber > 0) {
            link.append(prepareLink(uriBuilder, pageNumber - 1, pageSize, "prev"))
                    .append(",");
        }
        link.append(prepareLink(uriBuilder, page.getTotalPages() - 1, pageSize, "last"))
                .append(",")
                .append(prepareLink(uriBuilder, 0, pageSize, "first"));
        headers.add(HttpHeaders.LINK, link.toString());
        return headers;
    }

    private static String prepareLink(final UriComponentsBuilder uriBuilder, final int pageNumber, final int pageSize, final String relType) {
        return MessageFormat.format(HEADER_LINK_FORMAT, preparePageUri(uriBuilder, pageNumber, pageSize), relType);
    }

    private static String preparePageUri(final UriComponentsBuilder uriBuilder, final int pageNumber, final int pageSize) {
        return uriBuilder.replaceQueryParam("page", Integer.toString(pageNumber))
                .replaceQueryParam("size", Integer.toString(pageSize))
                .build()
                .toUriString()
                .replace(",", "%2C")
                .replace(";", "%3B");
    }
}