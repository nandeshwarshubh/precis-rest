-- Initial schema for URL shortening service
-- Creates the precis schema and URL_SHORTEN table

-- Create the precis schema if it doesn't exist
CREATE SCHEMA IF NOT EXISTS precis;

-- Create the URL_SHORTEN table in the precis schema
CREATE TABLE IF NOT EXISTS precis.url_shorten (
    short_url VARCHAR(8) PRIMARY KEY,
    long_url VARCHAR(2048) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP
);

-- Create index on long_url for duplicate detection and faster lookups
CREATE INDEX IF NOT EXISTS idx_long_url ON precis.url_shorten(long_url);

-- Add comments to table and columns
COMMENT ON TABLE precis.url_shorten IS 'Stores URL shortening mappings with lifecycle management';
COMMENT ON COLUMN precis.url_shorten.short_url IS 'Short URL identifier (8 characters, SHA-256 based)';
COMMENT ON COLUMN precis.url_shorten.long_url IS 'Original long URL';
COMMENT ON COLUMN precis.url_shorten.created_at IS 'Timestamp when the URL was created';
COMMENT ON COLUMN precis.url_shorten.expires_at IS 'Optional expiration timestamp for the URL';

