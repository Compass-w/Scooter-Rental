-- Migration: add city column to existing users table
-- Safe to run multiple times on PostgreSQL.

ALTER TABLE users
ADD COLUMN IF NOT EXISTS city VARCHAR(100);
