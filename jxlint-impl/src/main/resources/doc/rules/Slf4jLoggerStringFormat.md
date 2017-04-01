Java files that make use of SLF4J should generally favor parametrized logging
over `String.format` logging

For example:

    logger.info(String.format("Found %s", someVariable));

Could more easily be written as

    logger.info("Found {}", someVariable);

**Note**: the implementation does not fully parse symbols, so it approximates by
looking for SLF4J imports and usages of `LoggerFactory.getLogger`.
