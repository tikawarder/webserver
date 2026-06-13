-- Adds the @Version column to demo_orders for Optimistic Locking demo.
-- Hibernate uses this column to detect concurrent modification conflicts.
ALTER TABLE demo_orders ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
