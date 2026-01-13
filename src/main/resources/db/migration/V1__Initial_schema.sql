-- Initial schema for URL shortening service
-- Creates the URL_SHORTEN table with basic columns

CREATE TABLE IF NOT EXISTS url_shorten (
    short_url VARCHAR(8) PRIMARY KEY,
    long_url VARCHAR(2048) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP
);

-- Create index on long_url for duplicate detection and faster lookups
CREATE INDEX IF NOT EXISTS idx_long_url ON url_shorten(long_url);

-- Add comment to table
COMMENT ON TABLE url_shorten IS 'Stores URL shortening mappings with lifecycle management';
COMMENT ON COLUMN url_shorten.short_url IS 'Short URL identifier (8 characters, SHA-256 based)';
COMMENT ON COLUMN url_shorten.long_url IS 'Original long URL';
COMMENT ON COLUMN url_shorten.created_at IS 'Timestamp when the URL was created';
COMMENT ON COLUMN url_shorten.expires_at IS 'Optional expiration timestamp for the URL';

