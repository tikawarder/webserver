-- Composite index: equality column (status) first, range column (created_at) second.
CREATE INDEX idx_order_status_created_at ON demo_orders(status, created_at);

-- Postgres procedures can't return a result set directly; a set-returning
-- function is the idiomatic equivalent of a MySQL/Oracle/MSSQL stored procedure here.
CREATE OR REPLACE FUNCTION fn_revenue_by_city()
RETURNS TABLE(city_name VARCHAR, total_revenue DECIMAL) AS $$
BEGIN
    RETURN QUERY
    SELECT c.city AS city_name, SUM(o.amount) AS total_revenue
    FROM demo_orders o
    INNER JOIN demo_customers c ON o.customer_id = c.id
    GROUP BY c.city;
END;
$$ LANGUAGE plpgsql;
