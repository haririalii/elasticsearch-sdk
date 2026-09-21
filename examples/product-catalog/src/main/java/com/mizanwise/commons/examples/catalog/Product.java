package com.mizanwise.commons.examples.catalog;

/** Domain model owned by the application, not by the SDK. */
public record Product(String id, String name, String description, long price) {
}
