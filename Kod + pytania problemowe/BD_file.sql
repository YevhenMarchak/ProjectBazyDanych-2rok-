--
-- PostgreSQL database dump
--

-- Dumped from database version 17.5
-- Dumped by pg_dump version 17.5

-- Started on 2026-01-23 15:03:55

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 6 (class 2615 OID 25417)
-- Name: analytics; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA analytics;


ALTER SCHEMA analytics OWNER TO postgres;

--
-- TOC entry 7 (class 2615 OID 25418)
-- Name: pkg_categories; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA pkg_categories;


ALTER SCHEMA pkg_categories OWNER TO postgres;

--
-- TOC entry 5103 (class 0 OID 0)
-- Dependencies: 7
-- Name: SCHEMA pkg_categories; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA pkg_categories IS 'Logika CRUD dla kategorii produktów';


--
-- TOC entry 8 (class 2615 OID 25419)
-- Name: pkg_clients; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA pkg_clients;


ALTER SCHEMA pkg_clients OWNER TO postgres;

--
-- TOC entry 5104 (class 0 OID 0)
-- Dependencies: 8
-- Name: SCHEMA pkg_clients; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA pkg_clients IS 'Logika CRUD dla klientów';


--
-- TOC entry 9 (class 2615 OID 25420)
-- Name: pkg_employees; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA pkg_employees;


ALTER SCHEMA pkg_employees OWNER TO postgres;

--
-- TOC entry 5105 (class 0 OID 0)
-- Dependencies: 9
-- Name: SCHEMA pkg_employees; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA pkg_employees IS 'Logika CRUD dla pracowników';


--
-- TOC entry 10 (class 2615 OID 25421)
-- Name: pkg_inventory; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA pkg_inventory;


ALTER SCHEMA pkg_inventory OWNER TO postgres;

--
-- TOC entry 5106 (class 0 OID 0)
-- Dependencies: 10
-- Name: SCHEMA pkg_inventory; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA pkg_inventory IS 'Logika stanów magazynowych';


--
-- TOC entry 11 (class 2615 OID 25422)
-- Name: pkg_locations; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA pkg_locations;


ALTER SCHEMA pkg_locations OWNER TO postgres;

--
-- TOC entry 5107 (class 0 OID 0)
-- Dependencies: 11
-- Name: SCHEMA pkg_locations; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA pkg_locations IS 'Logika CRUD dla lokalizacji magazynowych';


--
-- TOC entry 12 (class 2615 OID 25423)
-- Name: pkg_products; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA pkg_products;


ALTER SCHEMA pkg_products OWNER TO postgres;

--
-- TOC entry 5108 (class 0 OID 0)
-- Dependencies: 12
-- Name: SCHEMA pkg_products; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA pkg_products IS 'Logika CRUD dla produktów';


--
-- TOC entry 13 (class 2615 OID 25424)
-- Name: pkg_receipts; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA pkg_receipts;


ALTER SCHEMA pkg_receipts OWNER TO postgres;

--
-- TOC entry 5109 (class 0 OID 0)
-- Dependencies: 13
-- Name: SCHEMA pkg_receipts; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA pkg_receipts IS 'Przyjęcia towaru i ich szczegóły';


--
-- TOC entry 14 (class 2615 OID 25425)
-- Name: pkg_shipments; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA pkg_shipments;


ALTER SCHEMA pkg_shipments OWNER TO postgres;

--
-- TOC entry 5110 (class 0 OID 0)
-- Dependencies: 14
-- Name: SCHEMA pkg_shipments; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA pkg_shipments IS 'Wydania towaru i ich szczegóły';


--
-- TOC entry 15 (class 2615 OID 25426)
-- Name: pkg_suppliers; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA pkg_suppliers;


ALTER SCHEMA pkg_suppliers OWNER TO postgres;

--
-- TOC entry 5111 (class 0 OID 0)
-- Dependencies: 15
-- Name: SCHEMA pkg_suppliers; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA pkg_suppliers IS 'Logika CRUD dla dostawców';


--
-- TOC entry 302 (class 1255 OID 25427)
-- Name: calculate_employee_bonus(integer, integer); Type: FUNCTION; Schema: analytics; Owner: postgres
--

CREATE FUNCTION analytics.calculate_employee_bonus(p_month integer, p_year integer) RETURNS TABLE(employee_id bigint, shipments_count integer, bonus_percent integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT
        s.employee_id,
        COUNT(*)::INT AS shipments_count,
        CASE
            WHEN COUNT(*) > 100 THEN 30
            WHEN COUNT(*) BETWEEN 50 AND 100 THEN 15
            ELSE 0
        END AS bonus_percent
    FROM public.shipments s
    WHERE EXTRACT(MONTH FROM s.shipment_date) = p_month
      AND EXTRACT(YEAR  FROM s.shipment_date) = p_year
      AND s.employee_id IS NOT NULL
    GROUP BY s.employee_id;
END;
$$;


ALTER FUNCTION analytics.calculate_employee_bonus(p_month integer, p_year integer) OWNER TO postgres;

--
-- TOC entry 247 (class 1255 OID 25428)
-- Name: check_location_capacity(bigint); Type: FUNCTION; Schema: analytics; Owner: postgres
--

CREATE FUNCTION analytics.check_location_capacity(p_location_id bigint) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
DECLARE
    total_quantity NUMERIC := 0;
    v_max_capacity NUMERIC;
BEGIN
    SELECT COALESCE(SUM(i.quantity), 0)
    INTO total_quantity
    FROM public.inventory i
    WHERE i.location_id = p_location_id;

    SELECT l.max_capacity
    INTO v_max_capacity
    FROM public.locations l
    WHERE l.location_id = p_location_id;

    -- jeśli brak limitu, uznajemy że OK
    IF v_max_capacity IS NULL THEN
        RETURN TRUE;
    END IF;

    RETURN total_quantity <= v_max_capacity;
END;
$$;


ALTER FUNCTION analytics.check_location_capacity(p_location_id bigint) OWNER TO postgres;

--
-- TOC entry 289 (class 1255 OID 25429)
-- Name: employee_productivity(); Type: FUNCTION; Schema: analytics; Owner: postgres
--

CREATE FUNCTION analytics.employee_productivity() RETURNS TABLE(employee_id bigint, clients_count integer, productivity_level text)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT
        s.employee_id,
        COUNT(DISTINCT s.client_id)::INT AS clients_count,
        CASE
            WHEN COUNT(DISTINCT s.client_id) > 20 THEN 'HIGH'
            WHEN COUNT(DISTINCT s.client_id) BETWEEN 10 AND 20 THEN 'MEDIUM'
            ELSE 'LOW'
        END AS productivity_level
    FROM public.shipments s
    WHERE s.employee_id IS NOT NULL
    GROUP BY s.employee_id;
END;
$$;


ALTER FUNCTION analytics.employee_productivity() OWNER TO postgres;

--
-- TOC entry 290 (class 1255 OID 25430)
-- Name: find_low_stock_products(numeric); Type: FUNCTION; Schema: analytics; Owner: postgres
--

CREATE FUNCTION analytics.find_low_stock_products(p_min_quantity numeric) RETURNS TABLE(product_id bigint, location_id bigint, quantity numeric)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT
        i.product_id,
        i.location_id,
        i.quantity
    FROM public.inventory i
    WHERE i.quantity < p_min_quantity;
END;
$$;


ALTER FUNCTION analytics.find_low_stock_products(p_min_quantity numeric) OWNER TO postgres;

--
-- TOC entry 269 (class 1255 OID 25431)
-- Name: get_top_clients(date, date, integer); Type: FUNCTION; Schema: analytics; Owner: postgres
--

CREATE FUNCTION analytics.get_top_clients(p_from date, p_to date, p_min_shipments integer) RETURNS TABLE(client_id bigint, shipments_count integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT
        s.client_id,
        COUNT(*)::INT AS shipments_count
    FROM public.shipments s
    WHERE s.shipment_date BETWEEN p_from AND p_to
    GROUP BY s.client_id
    HAVING COUNT(*) >= p_min_shipments;
END;
$$;


ALTER FUNCTION analytics.get_top_clients(p_from date, p_to date, p_min_shipments integer) OWNER TO postgres;

--
-- TOC entry 296 (class 1255 OID 25432)
-- Name: mark_overdue_shipments(); Type: PROCEDURE; Schema: analytics; Owner: postgres
--

CREATE PROCEDURE analytics.mark_overdue_shipments()
    LANGUAGE plpgsql
    AS $$
BEGIN
    UPDATE public.shipments
    SET status = 'OVERDUE'
    WHERE shipment_date < CURRENT_DATE - INTERVAL '7 days'
      AND status <> 'DELIVERED';
END;
$$;


ALTER PROCEDURE analytics.mark_overdue_shipments() OWNER TO postgres;

--
-- TOC entry 293 (class 1255 OID 25433)
-- Name: most_shipped_products(numeric); Type: FUNCTION; Schema: analytics; Owner: postgres
--

CREATE FUNCTION analytics.most_shipped_products(p_min_quantity numeric) RETURNS TABLE(product_id bigint, total_quantity numeric)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT
        sd.product_id,
        SUM(sd.quantity_to_ship) AS total_quantity
    FROM public.shipment_details sd
    GROUP BY sd.product_id
    HAVING SUM(sd.quantity_to_ship) >= p_min_quantity;
END;
$$;


ALTER FUNCTION analytics.most_shipped_products(p_min_quantity numeric) OWNER TO postgres;

--
-- TOC entry 253 (class 1255 OID 25434)
-- Name: receipt_completion_percentage(bigint); Type: FUNCTION; Schema: analytics; Owner: postgres
--

CREATE FUNCTION analytics.receipt_completion_percentage(p_receipt_id bigint) RETURNS numeric
    LANGUAGE plpgsql
    AS $$
DECLARE
    expected_sum NUMERIC := 0;
    received_sum NUMERIC := 0;
BEGIN
    SELECT
        COALESCE(SUM(expected_quantity), 0),
        COALESCE(SUM(received_quantity), 0)
    INTO expected_sum, received_sum
    FROM public.receipt_details
    WHERE receipt_id = p_receipt_id;

    IF expected_sum = 0 THEN
        RETURN 0;
    END IF;

    RETURN ROUND((received_sum / expected_sum) * 100, 2);
END;
$$;


ALTER FUNCTION analytics.receipt_completion_percentage(p_receipt_id bigint) OWNER TO postgres;

--
-- TOC entry 306 (class 1255 OID 25435)
-- Name: update_inventory_after_receipt(bigint); Type: PROCEDURE; Schema: analytics; Owner: postgres
--

CREATE PROCEDURE analytics.update_inventory_after_receipt(IN p_receipt_id bigint)
    LANGUAGE plpgsql
    AS $$
BEGIN
    UPDATE public.inventory i
    SET quantity = i.quantity + rd.received_quantity,
        last_updated = NOW()
    FROM public.receipt_details rd
    WHERE rd.receipt_id = p_receipt_id
      AND rd.product_id = i.product_id;
END;
$$;


ALTER PROCEDURE analytics.update_inventory_after_receipt(IN p_receipt_id bigint) OWNER TO postgres;

--
-- TOC entry 287 (class 1255 OID 25436)
-- Name: validate_receipt(bigint); Type: FUNCTION; Schema: analytics; Owner: postgres
--

CREATE FUNCTION analytics.validate_receipt(p_receipt_id bigint) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM public.receipt_details
        WHERE receipt_id = p_receipt_id
          AND received_quantity > expected_quantity
    ) THEN
        RETURN FALSE;
    END IF;

    RETURN TRUE;
END;
$$;


ALTER FUNCTION analytics.validate_receipt(p_receipt_id bigint) OWNER TO postgres;

--
-- TOC entry 264 (class 1255 OID 25437)
-- Name: categories_create(text, text); Type: PROCEDURE; Schema: pkg_categories; Owner: postgres
--

CREATE PROCEDURE pkg_categories.categories_create(IN p_name text, IN p_description text)
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- walidacja
    IF p_name IS NULL OR trim(p_name) = '' THEN
        RAISE EXCEPTION 'Category name cannot be empty';
    END IF;

    -- unikalność
    IF EXISTS (
        SELECT 1 FROM categories WHERE name = p_name
    ) THEN
        RAISE EXCEPTION 'Category with name "%" already exists', p_name;
    END IF;

    INSERT INTO categories(name, description)
    VALUES (p_name, p_description);

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Error while creating category: %', SQLERRM;
END;
$$;


ALTER PROCEDURE pkg_categories.categories_create(IN p_name text, IN p_description text) OWNER TO postgres;

--
-- TOC entry 317 (class 1255 OID 25438)
-- Name: categories_delete(bigint); Type: PROCEDURE; Schema: pkg_categories; Owner: postgres
--

CREATE PROCEDURE pkg_categories.categories_delete(IN p_category_id bigint)
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM categories WHERE category_id = p_category_id
    ) THEN
        RAISE EXCEPTION 'Category with id % does not exist', p_category_id;
    END IF;

    DELETE FROM categories WHERE category_id = p_category_id;

EXCEPTION
    WHEN foreign_key_violation THEN
        RAISE EXCEPTION 'Cannot delete category %, it is used by products', p_category_id;
END;
$$;


ALTER PROCEDURE pkg_categories.categories_delete(IN p_category_id bigint) OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 227 (class 1259 OID 25439)
-- Name: categories; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.categories (
    category_id bigint NOT NULL,
    name text NOT NULL,
    description text
);


ALTER TABLE public.categories OWNER TO postgres;

--
-- TOC entry 260 (class 1255 OID 25444)
-- Name: categories_read_all(); Type: FUNCTION; Schema: pkg_categories; Owner: postgres
--

CREATE FUNCTION pkg_categories.categories_read_all() RETURNS SETOF public.categories
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT * FROM categories;

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Error while reading categories: %', SQLERRM;
END;
$$;


ALTER FUNCTION pkg_categories.categories_read_all() OWNER TO postgres;

--
-- TOC entry 286 (class 1255 OID 25445)
-- Name: categories_read_one(bigint); Type: FUNCTION; Schema: pkg_categories; Owner: postgres
--

CREATE FUNCTION pkg_categories.categories_read_one(p_category_id bigint) RETURNS public.categories
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_row categories;
BEGIN
    -- walidacja parametru
    IF p_category_id IS NULL OR p_category_id <= 0 THEN
        RAISE EXCEPTION 'Invalid category id: %', p_category_id;
    END IF;

    SELECT *
    INTO v_row
    FROM categories
    WHERE category_id = p_category_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Category with id % not found', p_category_id;
    END IF;

    RETURN v_row;

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Error while reading category %: %',
            p_category_id, SQLERRM;
END;
$$;


ALTER FUNCTION pkg_categories.categories_read_one(p_category_id bigint) OWNER TO postgres;

--
-- TOC entry 285 (class 1255 OID 25446)
-- Name: categories_update(bigint, text, text); Type: PROCEDURE; Schema: pkg_categories; Owner: postgres
--

CREATE PROCEDURE pkg_categories.categories_update(IN p_category_id bigint, IN p_name text, IN p_description text)
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM categories WHERE category_id = p_category_id
    ) THEN
        RAISE EXCEPTION 'Category with id % does not exist', p_category_id;
    END IF;

    UPDATE categories
    SET name = COALESCE(p_name, name),
        description = COALESCE(p_description, description)
    WHERE category_id = p_category_id;
END;
$$;


ALTER PROCEDURE pkg_categories.categories_update(IN p_category_id bigint, IN p_name text, IN p_description text) OWNER TO postgres;

--
-- TOC entry 298 (class 1255 OID 25447)
-- Name: clients_create(text, text, text, text, text); Type: PROCEDURE; Schema: pkg_clients; Owner: postgres
--

CREATE PROCEDURE pkg_clients.clients_create(IN p_company_name text, IN p_delivery_address text, IN p_phone text, IN p_email text, IN p_tax_id text)
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- WALIDACJE
    IF p_company_name IS NULL OR trim(p_company_name) = '' THEN
        RAISE EXCEPTION 'Company name cannot be empty';
    END IF;

    IF p_email IS NOT NULL AND position('@' IN p_email) = 0 THEN
        RAISE EXCEPTION 'Invalid email format: %', p_email;
    END IF;

    IF p_tax_id IS NOT NULL AND length(p_tax_id) < 10 THEN
        RAISE EXCEPTION 'Tax ID is too short';
    END IF;

    INSERT INTO clients(
        company_name,
        delivery_address,
        phone,
        email,
        tax_id
    )
    VALUES (
        p_company_name,
        p_delivery_address,
        p_phone,
        p_email,
        p_tax_id
    );

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Error while creating client: %', SQLERRM;
END;
$$;


ALTER PROCEDURE pkg_clients.clients_create(IN p_company_name text, IN p_delivery_address text, IN p_phone text, IN p_email text, IN p_tax_id text) OWNER TO postgres;

--
-- TOC entry 295 (class 1255 OID 25448)
-- Name: clients_delete(bigint); Type: PROCEDURE; Schema: pkg_clients; Owner: postgres
--

CREATE PROCEDURE pkg_clients.clients_delete(IN p_client_id bigint)
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF p_client_id IS NULL OR p_client_id <= 0 THEN
        RAISE EXCEPTION 'Invalid client id: %', p_client_id;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM clients WHERE client_id = p_client_id
    ) THEN
        RAISE EXCEPTION 'Client with id % does not exist', p_client_id;
    END IF;

    DELETE FROM clients WHERE client_id = p_client_id;

EXCEPTION
    WHEN foreign_key_violation THEN
        RAISE EXCEPTION
            'Cannot delete client %, client is used in shipments',
            p_client_id;
END;
$$;


ALTER PROCEDURE pkg_clients.clients_delete(IN p_client_id bigint) OWNER TO postgres;

--
-- TOC entry 228 (class 1259 OID 25449)
-- Name: clients; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.clients (
    client_id bigint NOT NULL,
    company_name text NOT NULL,
    delivery_address text,
    phone text,
    email text,
    tax_id text
);


ALTER TABLE public.clients OWNER TO postgres;

--
-- TOC entry 308 (class 1255 OID 25454)
-- Name: clients_read_all(); Type: FUNCTION; Schema: pkg_clients; Owner: postgres
--

CREATE FUNCTION pkg_clients.clients_read_all() RETURNS SETOF public.clients
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT * FROM clients;

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Error while reading clients: %', SQLERRM;
END;
$$;


ALTER FUNCTION pkg_clients.clients_read_all() OWNER TO postgres;

--
-- TOC entry 281 (class 1255 OID 25455)
-- Name: clients_read_one(bigint); Type: FUNCTION; Schema: pkg_clients; Owner: postgres
--

CREATE FUNCTION pkg_clients.clients_read_one(p_client_id bigint) RETURNS public.clients
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_row clients;
BEGIN
    IF p_client_id IS NULL OR p_client_id <= 0 THEN
        RAISE EXCEPTION 'Invalid client id: %', p_client_id;
    END IF;

    SELECT *
    INTO v_row
    FROM clients
    WHERE client_id = p_client_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Client with id % not found', p_client_id;
    END IF;

    RETURN v_row;

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION
            'Error while reading client %: %',
            p_client_id, SQLERRM;
END;
$$;


ALTER FUNCTION pkg_clients.clients_read_one(p_client_id bigint) OWNER TO postgres;

--
-- TOC entry 275 (class 1255 OID 25456)
-- Name: clients_update(bigint, text, text, text, text, text); Type: PROCEDURE; Schema: pkg_clients; Owner: postgres
--

CREATE PROCEDURE pkg_clients.clients_update(IN p_client_id bigint, IN p_company_name text, IN p_delivery_address text, IN p_phone text, IN p_email text, IN p_tax_id text)
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- WALIDACJA ID
    IF p_client_id IS NULL OR p_client_id <= 0 THEN
        RAISE EXCEPTION 'Invalid client id: %', p_client_id;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM clients WHERE client_id = p_client_id
    ) THEN
        RAISE EXCEPTION 'Client with id % does not exist', p_client_id;
    END IF;

    IF p_email IS NOT NULL AND position('@' IN p_email) = 0 THEN
        RAISE EXCEPTION 'Invalid email format: %', p_email;
    END IF;

    UPDATE clients
    SET company_name     = COALESCE(p_company_name, company_name),
        delivery_address = COALESCE(p_delivery_address, delivery_address),
        phone            = COALESCE(p_phone, phone),
        email            = COALESCE(p_email, email),
        tax_id           = COALESCE(p_tax_id, tax_id)
    WHERE client_id = p_client_id;
END;
$$;


ALTER PROCEDURE pkg_clients.clients_update(IN p_client_id bigint, IN p_company_name text, IN p_delivery_address text, IN p_phone text, IN p_email text, IN p_tax_id text) OWNER TO postgres;

--
-- TOC entry 250 (class 1255 OID 25457)
-- Name: employees_create(text, text, character varying, date, text, text); Type: PROCEDURE; Schema: pkg_employees; Owner: postgres
--

CREATE PROCEDURE pkg_employees.employees_create(IN p_first_name text, IN p_last_name text, IN p_position character varying, IN p_hire_date date, IN p_phone text, IN p_email text)
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- WALIDACJE
    IF p_first_name IS NULL OR trim(p_first_name) = '' THEN
        RAISE EXCEPTION 'First name cannot be empty';
    END IF;

    IF p_last_name IS NULL OR trim(p_last_name) = '' THEN
        RAISE EXCEPTION 'Last name cannot be empty';
    END IF;

    IF p_position IS NULL OR trim(p_position) = '' THEN
        RAISE EXCEPTION 'Position cannot be empty';
    END IF;

    IF p_hire_date IS NOT NULL AND p_hire_date > CURRENT_DATE THEN
        RAISE EXCEPTION 'Hire date cannot be in the future';
    END IF;

    IF p_email IS NOT NULL AND position('@' IN p_email) = 0 THEN
        RAISE EXCEPTION 'Invalid email format: %', p_email;
    END IF;

    INSERT INTO employees(
        first_name,
        last_name,
        position,
        hire_date,
        phone,
        email
    )
    VALUES (
        p_first_name,
        p_last_name,
        p_position,
        p_hire_date,
        p_phone,
        p_email
    );

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Error while creating employee: %', SQLERRM;
END;
$$;


ALTER PROCEDURE pkg_employees.employees_create(IN p_first_name text, IN p_last_name text, IN p_position character varying, IN p_hire_date date, IN p_phone text, IN p_email text) OWNER TO postgres;

--
-- TOC entry 311 (class 1255 OID 25458)
-- Name: employees_delete(bigint); Type: PROCEDURE; Schema: pkg_employees; Owner: postgres
--

CREATE PROCEDURE pkg_employees.employees_delete(IN p_employee_id bigint)
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF p_employee_id IS NULL OR p_employee_id <= 0 THEN
        RAISE EXCEPTION 'Invalid employee id: %', p_employee_id;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM employees WHERE employee_id = p_employee_id
    ) THEN
        RAISE EXCEPTION 'Employee with id % does not exist', p_employee_id;
    END IF;

    DELETE FROM employees WHERE employee_id = p_employee_id;

EXCEPTION
    WHEN foreign_key_violation THEN
        RAISE EXCEPTION
            'Cannot delete employee %, employee is used in receipts or shipments',
            p_employee_id;
END;
$$;


ALTER PROCEDURE pkg_employees.employees_delete(IN p_employee_id bigint) OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 25459)
-- Name: employees; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.employees (
    employee_id bigint NOT NULL,
    first_name text NOT NULL,
    last_name text NOT NULL,
    "position" character varying(100) NOT NULL,
    hire_date date,
    phone text,
    email text
);


ALTER TABLE public.employees OWNER TO postgres;

--
-- TOC entry 321 (class 1255 OID 25464)
-- Name: employees_read_all(); Type: FUNCTION; Schema: pkg_employees; Owner: postgres
--

CREATE FUNCTION pkg_employees.employees_read_all() RETURNS SETOF public.employees
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT * FROM employees;

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Error while reading employees: %', SQLERRM;
END;
$$;


ALTER FUNCTION pkg_employees.employees_read_all() OWNER TO postgres;

--
-- TOC entry 307 (class 1255 OID 25465)
-- Name: employees_read_one(bigint); Type: FUNCTION; Schema: pkg_employees; Owner: postgres
--

CREATE FUNCTION pkg_employees.employees_read_one(p_employee_id bigint) RETURNS public.employees
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_row employees;
BEGIN
    IF p_employee_id IS NULL OR p_employee_id <= 0 THEN
        RAISE EXCEPTION 'Invalid employee id: %', p_employee_id;
    END IF;

    SELECT *
    INTO v_row
    FROM employees
    WHERE employee_id = p_employee_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Employee with id % not found', p_employee_id;
    END IF;

    RETURN v_row;

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION
            'Error while reading employee %: %',
            p_employee_id, SQLERRM;
END;
$$;


ALTER FUNCTION pkg_employees.employees_read_one(p_employee_id bigint) OWNER TO postgres;

--
-- TOC entry 304 (class 1255 OID 25466)
-- Name: employees_update(bigint, text, text, character varying, date, text, text); Type: PROCEDURE; Schema: pkg_employees; Owner: postgres
--

CREATE PROCEDURE pkg_employees.employees_update(IN p_employee_id bigint, IN p_first_name text, IN p_last_name text, IN p_position character varying, IN p_hire_date date, IN p_phone text, IN p_email text)
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- WALIDACJA ID
    IF p_employee_id IS NULL OR p_employee_id <= 0 THEN
        RAISE EXCEPTION 'Invalid employee id: %', p_employee_id;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM employees WHERE employee_id = p_employee_id
    ) THEN
        RAISE EXCEPTION 'Employee with id % does not exist', p_employee_id;
    END IF;

    IF p_hire_date IS NOT NULL AND p_hire_date > CURRENT_DATE THEN
        RAISE EXCEPTION 'Hire date cannot be in the future';
    END IF;

    IF p_email IS NOT NULL AND position('@' IN p_email) = 0 THEN
        RAISE EXCEPTION 'Invalid email format: %', p_email;
    END IF;

    UPDATE employees
    SET first_name = COALESCE(p_first_name, first_name),
        last_name  = COALESCE(p_last_name, last_name),
        position   = COALESCE(p_position, position),
        hire_date  = COALESCE(p_hire_date, hire_date),
        phone      = COALESCE(p_phone, phone),
        email      = COALESCE(p_email, email)
    WHERE employee_id = p_employee_id;
END;
$$;


ALTER PROCEDURE pkg_employees.employees_update(IN p_employee_id bigint, IN p_first_name text, IN p_last_name text, IN p_position character varying, IN p_hire_date date, IN p_phone text, IN p_email text) OWNER TO postgres;

--
-- TOC entry 288 (class 1255 OID 25467)
-- Name: inventory_create(bigint, bigint, numeric); Type: PROCEDURE; Schema: pkg_inventory; Owner: postgres
--

CREATE PROCEDURE pkg_inventory.inventory_create(IN p_product_id bigint, IN p_location_id bigint, IN p_quantity numeric)
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- WALIDACJE ID
    IF p_product_id IS NULL OR p_product_id <= 0 THEN
        RAISE EXCEPTION 'Invalid product id: %', p_product_id;
    END IF;

    IF p_location_id IS NULL OR p_location_id <= 0 THEN
        RAISE EXCEPTION 'Invalid location id: %', p_location_id;
    END IF;

    -- WALIDACJA ILOŚCI
    IF p_quantity IS NULL OR p_quantity < 0 THEN
        RAISE EXCEPTION 'Quantity must be >= 0';
    END IF;

    -- SPRAWDZENIE FK
    IF NOT EXISTS (SELECT 1 FROM products WHERE product_id = p_product_id) THEN
        RAISE EXCEPTION 'Product with id % does not exist', p_product_id;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM locations WHERE location_id = p_location_id) THEN
        RAISE EXCEPTION 'Location with id % does not exist', p_location_id;
    END IF;

    -- UNIKALNOŚĆ (product + location)
    IF EXISTS (
        SELECT 1
        FROM inventory
        WHERE product_id = p_product_id
          AND location_id = p_location_id
    ) THEN
        RAISE EXCEPTION
            'Inventory entry for product % at location % already exists',
            p_product_id, p_location_id;
    END IF;

    INSERT INTO inventory(product_id, location_id, quantity)
    VALUES (p_product_id, p_location_id, p_quantity);

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Error while creating inventory entry: %', SQLERRM;
END;
$$;


ALTER PROCEDURE pkg_inventory.inventory_create(IN p_product_id bigint, IN p_location_id bigint, IN p_quantity numeric) OWNER TO postgres;

--
-- TOC entry 276 (class 1255 OID 25468)
-- Name: inventory_delete(bigint); Type: PROCEDURE; Schema: pkg_inventory; Owner: postgres
--

CREATE PROCEDURE pkg_inventory.inventory_delete(IN p_inventory_id bigint)
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF p_inventory_id IS NULL OR p_inventory_id <= 0 THEN
        RAISE EXCEPTION 'Invalid inventory id: %', p_inventory_id;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM inventory WHERE inventory_id = p_inventory_id
    ) THEN
        RAISE EXCEPTION 'Inventory entry with id % does not exist', p_inventory_id;
    END IF;

    DELETE FROM inventory WHERE inventory_id = p_inventory_id;
END;
$$;


ALTER PROCEDURE pkg_inventory.inventory_delete(IN p_inventory_id bigint) OWNER TO postgres;

--
-- TOC entry 230 (class 1259 OID 25469)
-- Name: inventory; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.inventory (
    inventory_id bigint NOT NULL,
    product_id bigint NOT NULL,
    location_id bigint NOT NULL,
    quantity numeric(14,3) NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    CONSTRAINT inventory_quantity_check CHECK ((quantity >= (0)::numeric))
);


ALTER TABLE public.inventory OWNER TO postgres;

--
-- TOC entry 251 (class 1255 OID 25474)
-- Name: inventory_read_all(); Type: FUNCTION; Schema: pkg_inventory; Owner: postgres
--

CREATE FUNCTION pkg_inventory.inventory_read_all() RETURNS SETOF public.inventory
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT * FROM inventory;

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Error while reading inventory: %', SQLERRM;
END;
$$;


ALTER FUNCTION pkg_inventory.inventory_read_all() OWNER TO postgres;

--
-- TOC entry 248 (class 1255 OID 25475)
-- Name: inventory_read_one(bigint); Type: FUNCTION; Schema: pkg_inventory; Owner: postgres
--

CREATE FUNCTION pkg_inventory.inventory_read_one(p_inventory_id bigint) RETURNS public.inventory
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_row inventory;
BEGIN
    IF p_inventory_id IS NULL OR p_inventory_id <= 0 THEN
        RAISE EXCEPTION 'Invalid inventory id: %', p_inventory_id;
    END IF;

    SELECT *
    INTO v_row
    FROM inventory
    WHERE inventory_id = p_inventory_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Inventory entry with id % not found', p_inventory_id;
    END IF;

    RETURN v_row;

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION
            'Error while reading inventory entry %: %',
            p_inventory_id, SQLERRM;
END;
$$;


ALTER FUNCTION pkg_inventory.inventory_read_one(p_inventory_id bigint) OWNER TO postgres;

--
-- TOC entry 292 (class 1255 OID 25476)
-- Name: inventory_update(bigint, bigint, bigint, numeric); Type: PROCEDURE; Schema: pkg_inventory; Owner: postgres
--

CREATE PROCEDURE pkg_inventory.inventory_update(IN p_inventory_id bigint, IN p_product_id bigint, IN p_location_id bigint, IN p_quantity numeric)
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- WALIDACJA ID
    IF p_inventory_id IS NULL OR p_inventory_id <= 0 THEN
        RAISE EXCEPTION 'Invalid inventory id: %', p_inventory_id;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM inventory WHERE inventory_id = p_inventory_id
    ) THEN
        RAISE EXCEPTION 'Inventory entry with id % does not exist', p_inventory_id;
    END IF;

    IF p_quantity IS NOT NULL AND p_quantity < 0 THEN
        RAISE EXCEPTION 'Quantity must be >= 0';
    END IF;

    UPDATE inventory
    SET product_id   = COALESCE(p_product_id, product_id),
        location_id  = COALESCE(p_location_id, location_id),
        quantity     = COALESCE(p_quantity, quantity),
        last_updated = NOW()
    WHERE inventory_id = p_inventory_id;

EXCEPTION
    WHEN unique_violation THEN
        RAISE EXCEPTION
            'Inventory entry for this product and location already exists';
END;
$$;


ALTER PROCEDURE pkg_inventory.inventory_update(IN p_inventory_id bigint, IN p_product_id bigint, IN p_location_id bigint, IN p_quantity numeric) OWNER TO postgres;

--
-- TOC entry 263 (class 1255 OID 25477)
-- Name: locations_create(text, character varying, numeric); Type: PROCEDURE; Schema: pkg_locations; Owner: postgres
--

CREATE PROCEDURE pkg_locations.locations_create(IN p_location_code text, IN p_location_type character varying, IN p_max_capacity numeric)
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- WALIDACJE
    IF p_location_code IS NULL OR trim(p_location_code) = '' THEN
        RAISE EXCEPTION 'Location code cannot be empty';
    END IF;

    IF p_location_type IS NULL OR trim(p_location_type) = '' THEN
        RAISE EXCEPTION 'Location type cannot be empty';
    END IF;

    IF p_max_capacity IS NOT NULL AND p_max_capacity < 0 THEN
        RAISE EXCEPTION 'Max capacity must be >= 0';
    END IF;

    -- UNIKALNOŚĆ
    IF EXISTS (
        SELECT 1 FROM locations WHERE location_code = p_location_code
    ) THEN
        RAISE EXCEPTION
            'Location with code "%" already exists',
            p_location_code;
    END IF;

    INSERT INTO locations(
        location_code,
        location_type,
        max_capacity
    )
    VALUES (
        p_location_code,
        p_location_type,
        p_max_capacity
    );

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Error while creating location: %', SQLERRM;
END;
$$;


ALTER PROCEDURE pkg_locations.locations_create(IN p_location_code text, IN p_location_type character varying, IN p_max_capacity numeric) OWNER TO postgres;

--
-- TOC entry 312 (class 1255 OID 25478)
-- Name: locations_delete(bigint); Type: PROCEDURE; Schema: pkg_locations; Owner: postgres
--

CREATE PROCEDURE pkg_locations.locations_delete(IN p_location_id bigint)
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF p_location_id IS NULL OR p_location_id <= 0 THEN
        RAISE EXCEPTION 'Invalid location id: %', p_location_id;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM locations WHERE location_id = p_location_id
    ) THEN
        RAISE EXCEPTION 'Location with id % does not exist', p_location_id;
    END IF;

    DELETE FROM locations WHERE location_id = p_location_id;

EXCEPTION
    WHEN foreign_key_violation THEN
        RAISE EXCEPTION
            'Cannot delete location %, it is used in inventory',
            p_location_id;
END;
$$;


ALTER PROCEDURE pkg_locations.locations_delete(IN p_location_id bigint) OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 25479)
-- Name: locations; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.locations (
    location_id bigint NOT NULL,
    location_code text NOT NULL,
    location_type character varying(50) NOT NULL,
    max_capacity numeric(12,3),
    CONSTRAINT locations_max_capacity_check CHECK ((max_capacity >= (0)::numeric))
);


ALTER TABLE public.locations OWNER TO postgres;

--
-- TOC entry 284 (class 1255 OID 25485)
-- Name: locations_read_all(); Type: FUNCTION; Schema: pkg_locations; Owner: postgres
--

CREATE FUNCTION pkg_locations.locations_read_all() RETURNS SETOF public.locations
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT * FROM locations;

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Error while reading locations: %', SQLERRM;
END;
$$;


ALTER FUNCTION pkg_locations.locations_read_all() OWNER TO postgres;

--
-- TOC entry 278 (class 1255 OID 25486)
-- Name: locations_read_one(bigint); Type: FUNCTION; Schema: pkg_locations; Owner: postgres
--

CREATE FUNCTION pkg_locations.locations_read_one(p_location_id bigint) RETURNS public.locations
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_row locations;
BEGIN
    IF p_location_id IS NULL OR p_location_id <= 0 THEN
        RAISE EXCEPTION 'Invalid location id: %', p_location_id;
    END IF;

    SELECT *
    INTO v_row
    FROM locations
    WHERE location_id = p_location_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Location with id % not found', p_location_id;
    END IF;

    RETURN v_row;

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION
            'Error while reading location %: %',
            p_location_id, SQLERRM;
END;
$$;


ALTER FUNCTION pkg_locations.locations_read_one(p_location_id bigint) OWNER TO postgres;

--
-- TOC entry 257 (class 1255 OID 25487)
-- Name: locations_update(bigint, text, character varying, numeric); Type: PROCEDURE; Schema: pkg_locations; Owner: postgres
--

CREATE PROCEDURE pkg_locations.locations_update(IN p_location_id bigint, IN p_location_code text, IN p_location_type character varying, IN p_max_capacity numeric)
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- WALIDACJA ID
    IF p_location_id IS NULL OR p_location_id <= 0 THEN
        RAISE EXCEPTION 'Invalid location id: %', p_location_id;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM locations WHERE location_id = p_location_id
    ) THEN
        RAISE EXCEPTION 'Location with id % does not exist', p_location_id;
    END IF;

    IF p_max_capacity IS NOT NULL AND p_max_capacity < 0 THEN
        RAISE EXCEPTION 'Max capacity must be >= 0';
    END IF;

    UPDATE locations
    SET location_code = COALESCE(p_location_code, location_code),
        location_type = COALESCE(p_location_type, location_type),
        max_capacity  = COALESCE(p_max_capacity, max_capacity)
    WHERE location_id = p_location_id;

EXCEPTION
    WHEN unique_violation THEN
        RAISE EXCEPTION 'Location code must be unique';
END;
$$;


ALTER PROCEDURE pkg_locations.locations_update(IN p_location_id bigint, IN p_location_code text, IN p_location_type character varying, IN p_max_capacity numeric) OWNER TO postgres;

--
-- TOC entry 272 (class 1255 OID 25488)
-- Name: products_create(text, text, text, bigint, bigint, numeric, text); Type: PROCEDURE; Schema: pkg_products; Owner: postgres
--

CREATE PROCEDURE pkg_products.products_create(IN p_sku text, IN p_name text, IN p_description text, IN p_category_id bigint, IN p_supplier_id bigint, IN p_weight numeric, IN p_dimensions text)
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- WALIDACJE PODSTAWOWE
    IF p_sku IS NULL OR trim(p_sku) = '' THEN
        RAISE EXCEPTION 'SKU cannot be empty';
    END IF;

    IF p_name IS NULL OR trim(p_name) = '' THEN
        RAISE EXCEPTION 'Product name cannot be empty';
    END IF;

    IF p_category_id IS NULL OR p_category_id <= 0 THEN
        RAISE EXCEPTION 'Invalid category id: %', p_category_id;
    END IF;

    IF p_weight IS NOT NULL AND p_weight < 0 THEN
        RAISE EXCEPTION 'Weight must be >= 0';
    END IF;

    -- UNIKALNOŚĆ SKU
    IF EXISTS (
        SELECT 1 FROM products WHERE sku = p_sku
    ) THEN
        RAISE EXCEPTION 'Product with SKU "%" already exists', p_sku;
    END IF;

    -- FK
    IF NOT EXISTS (
        SELECT 1 FROM categories WHERE category_id = p_category_id
    ) THEN
        RAISE EXCEPTION 'Category with id % does not exist', p_category_id;
    END IF;

    IF p_supplier_id IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM suppliers WHERE supplier_id = p_supplier_id
    ) THEN
        RAISE EXCEPTION 'Supplier with id % does not exist', p_supplier_id;
    END IF;

    INSERT INTO products(
        sku,
        name,
        description,
        category_id,
        supplier_id,
        weight,
        dimensions
    )
    VALUES (
        p_sku,
        p_name,
        p_description,
        p_category_id,
        p_supplier_id,
        p_weight,
        p_dimensions
    );

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Error while creating product: %', SQLERRM;
END;
$$;


ALTER PROCEDURE pkg_products.products_create(IN p_sku text, IN p_name text, IN p_description text, IN p_category_id bigint, IN p_supplier_id bigint, IN p_weight numeric, IN p_dimensions text) OWNER TO postgres;

--
-- TOC entry 320 (class 1255 OID 25489)
-- Name: products_delete(bigint); Type: PROCEDURE; Schema: pkg_products; Owner: postgres
--

CREATE PROCEDURE pkg_products.products_delete(IN p_product_id bigint)
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF p_product_id IS NULL OR p_product_id <= 0 THEN
        RAISE EXCEPTION 'Invalid product id: %', p_product_id;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM products WHERE product_id = p_product_id
    ) THEN
        RAISE EXCEPTION 'Product with id % does not exist', p_product_id;
    END IF;

    DELETE FROM products WHERE product_id = p_product_id;

EXCEPTION
    WHEN foreign_key_violation THEN
        RAISE EXCEPTION
            'Cannot delete product %, it is used in inventory, receipts or shipments',
            p_product_id;
END;
$$;


ALTER PROCEDURE pkg_products.products_delete(IN p_product_id bigint) OWNER TO postgres;

--
-- TOC entry 232 (class 1259 OID 25490)
-- Name: products; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.products (
    product_id bigint NOT NULL,
    sku text NOT NULL,
    name text NOT NULL,
    description text,
    category_id bigint NOT NULL,
    supplier_id bigint,
    weight numeric(10,3),
    dimensions text,
    CONSTRAINT products_weight_check CHECK ((weight >= (0)::numeric))
);


ALTER TABLE public.products OWNER TO postgres;

--
-- TOC entry 279 (class 1255 OID 25496)
-- Name: products_read_all(); Type: FUNCTION; Schema: pkg_products; Owner: postgres
--

CREATE FUNCTION pkg_products.products_read_all() RETURNS SETOF public.products
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT * FROM products;

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Error while reading products: %', SQLERRM;
END;
$$;


ALTER FUNCTION pkg_products.products_read_all() OWNER TO postgres;

--
-- TOC entry 249 (class 1255 OID 25497)
-- Name: products_read_one(bigint); Type: FUNCTION; Schema: pkg_products; Owner: postgres
--

CREATE FUNCTION pkg_products.products_read_one(p_product_id bigint) RETURNS public.products
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_row products;
BEGIN
    IF p_product_id IS NULL OR p_product_id <= 0 THEN
        RAISE EXCEPTION 'Invalid product id: %', p_product_id;
    END IF;

    SELECT *
    INTO v_row
    FROM products
    WHERE product_id = p_product_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Product with id % not found', p_product_id;
    END IF;

    RETURN v_row;

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION
            'Error while reading product %: %',
            p_product_id, SQLERRM;
END;
$$;


ALTER FUNCTION pkg_products.products_read_one(p_product_id bigint) OWNER TO postgres;

--
-- TOC entry 267 (class 1255 OID 25498)
-- Name: products_update(bigint, text, text, text, bigint, bigint, numeric, text); Type: PROCEDURE; Schema: pkg_products; Owner: postgres
--

CREATE PROCEDURE pkg_products.products_update(IN p_product_id bigint, IN p_sku text, IN p_name text, IN p_description text, IN p_category_id bigint, IN p_supplier_id bigint, IN p_weight numeric, IN p_dimensions text)
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- WALIDACJA ID
    IF p_product_id IS NULL OR p_product_id <= 0 THEN
        RAISE EXCEPTION 'Invalid product id: %', p_product_id;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM products WHERE product_id = p_product_id
    ) THEN
        RAISE EXCEPTION 'Product with id % does not exist', p_product_id;
    END IF;

    IF p_weight IS NOT NULL AND p_weight < 0 THEN
        RAISE EXCEPTION 'Weight must be >= 0';
    END IF;

    UPDATE products
    SET sku         = COALESCE(p_sku, sku),
        name        = COALESCE(p_name, name),
        description = COALESCE(p_description, description),
        category_id = COALESCE(p_category_id, category_id),
        supplier_id = COALESCE(p_supplier_id, supplier_id),
        weight      = COALESCE(p_weight, weight),
        dimensions  = COALESCE(p_dimensions, dimensions)
    WHERE product_id = p_product_id;

EXCEPTION
    WHEN unique_violation THEN
        RAISE EXCEPTION 'SKU must be unique';
    WHEN foreign_key_violation THEN
        RAISE EXCEPTION 'Invalid category or supplier reference';
END;
$$;


ALTER PROCEDURE pkg_products.products_update(IN p_product_id bigint, IN p_sku text, IN p_name text, IN p_description text, IN p_category_id bigint, IN p_supplier_id bigint, IN p_weight numeric, IN p_dimensions text) OWNER TO postgres;

--
-- TOC entry 259 (class 1255 OID 25499)
-- Name: receipt_details_create(bigint, bigint, numeric, numeric); Type: PROCEDURE; Schema: pkg_receipts; Owner: postgres
--

CREATE PROCEDURE pkg_receipts.receipt_details_create(IN p_receipt_id bigint, IN p_product_id bigint, IN p_expected_quantity numeric, IN p_received_quantity numeric)
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- WALIDACJA ID
    IF p_receipt_id IS NULL OR p_receipt_id <= 0 THEN
        RAISE EXCEPTION 'Invalid receipt id: %', p_receipt_id;
    END IF;

    IF p_product_id IS NULL OR p_product_id <= 0 THEN
        RAISE EXCEPTION 'Invalid product id: %', p_product_id;
    END IF;

    -- WALIDACJA ILOŚCI
    IF p_expected_quantity IS NULL OR p_expected_quantity < 0 THEN
        RAISE EXCEPTION 'Expected quantity must be >= 0';
    END IF;

    IF p_received_quantity IS NULL OR p_received_quantity < 0 THEN
        RAISE EXCEPTION 'Received quantity must be >= 0';
    END IF;

    -- FK
    IF NOT EXISTS (SELECT 1 FROM receipts WHERE receipt_id = p_receipt_id) THEN
        RAISE EXCEPTION 'Receipt with id % does not exist', p_receipt_id;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM products WHERE product_id = p_product_id) THEN
        RAISE EXCEPTION 'Product with id % does not exist', p_product_id;
    END IF;

    -- UNIKALNOŚĆ (PK ZŁOŻONY)
    IF EXISTS (
        SELECT 1
        FROM receipt_details
        WHERE receipt_id = p_receipt_id
          AND product_id = p_product_id
    ) THEN
        RAISE EXCEPTION
            'Receipt detail for receipt % and product % already exists',
            p_receipt_id, p_product_id;
    END IF;

    INSERT INTO receipt_details(
        receipt_id,
        product_id,
        expected_quantity,
        received_quantity
    )
    VALUES (
        p_receipt_id,
        p_product_id,
        p_expected_quantity,
        p_received_quantity
    );

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Error while creating receipt detail: %', SQLERRM;
END;
$$;


ALTER PROCEDURE pkg_receipts.receipt_details_create(IN p_receipt_id bigint, IN p_product_id bigint, IN p_expected_quantity numeric, IN p_received_quantity numeric) OWNER TO postgres;

--
-- TOC entry 262 (class 1255 OID 25500)
-- Name: receipt_details_delete(bigint, bigint); Type: PROCEDURE; Schema: pkg_receipts; Owner: postgres
--

CREATE PROCEDURE pkg_receipts.receipt_details_delete(IN p_receipt_id bigint, IN p_product_id bigint)
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF p_receipt_id IS NULL OR p_receipt_id <= 0 THEN
        RAISE EXCEPTION 'Invalid receipt id: %', p_receipt_id;
    END IF;

    IF p_product_id IS NULL OR p_product_id <= 0 THEN
        RAISE EXCEPTION 'Invalid product id: %', p_product_id;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM receipt_details
        WHERE receipt_id = p_receipt_id
          AND product_id = p_product_id
    ) THEN
        RAISE EXCEPTION
            'Receipt detail for receipt % and product % does not exist',
            p_receipt_id, p_product_id;
    END IF;

    DELETE FROM receipt_details
    WHERE receipt_id = p_receipt_id
      AND product_id = p_product_id;
END;
$$;


ALTER PROCEDURE pkg_receipts.receipt_details_delete(IN p_receipt_id bigint, IN p_product_id bigint) OWNER TO postgres;

--
-- TOC entry 233 (class 1259 OID 25501)
-- Name: receipt_details; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.receipt_details (
    receipt_id bigint NOT NULL,
    product_id bigint NOT NULL,
    expected_quantity numeric(14,3) NOT NULL,
    received_quantity numeric(14,3) NOT NULL,
    CONSTRAINT receipt_details_expected_quantity_check CHECK ((expected_quantity >= (0)::numeric)),
    CONSTRAINT receipt_details_received_quantity_check CHECK ((received_quantity >= (0)::numeric))
);


ALTER TABLE public.receipt_details OWNER TO postgres;

--
-- TOC entry 305 (class 1255 OID 25506)
-- Name: receipt_details_read_all(); Type: FUNCTION; Schema: pkg_receipts; Owner: postgres
--

CREATE FUNCTION pkg_receipts.receipt_details_read_all() RETURNS SETOF public.receipt_details
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT * FROM receipt_details;

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Error while reading receipt details: %', SQLERRM;
END;
$$;


ALTER FUNCTION pkg_receipts.receipt_details_read_all() OWNER TO postgres;

--
-- TOC entry 310 (class 1255 OID 25507)
-- Name: receipt_details_read_one(bigint, bigint); Type: FUNCTION; Schema: pkg_receipts; Owner: postgres
--

CREATE FUNCTION pkg_receipts.receipt_details_read_one(p_receipt_id bigint, p_product_id bigint) RETURNS public.receipt_details
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_row receipt_details;
BEGIN
    IF p_receipt_id IS NULL OR p_receipt_id <= 0 THEN
        RAISE EXCEPTION 'Invalid receipt id: %', p_receipt_id;
    END IF;

    IF p_product_id IS NULL OR p_product_id <= 0 THEN
        RAISE EXCEPTION 'Invalid product id: %', p_product_id;
    END IF;

    SELECT *
    INTO v_row
    FROM receipt_details
    WHERE receipt_id = p_receipt_id
      AND product_id = p_product_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'Receipt detail for receipt % and product % not found',
            p_receipt_id, p_product_id;
    END IF;

    RETURN v_row;

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION
            'Error while reading receipt detail (% , %): %',
            p_receipt_id, p_product_id, SQLERRM;
END;
$$;


ALTER FUNCTION pkg_receipts.receipt_details_read_one(p_receipt_id bigint, p_product_id bigint) OWNER TO postgres;

--
-- TOC entry 254 (class 1255 OID 25508)
-- Name: receipt_details_update(bigint, bigint, numeric, numeric); Type: PROCEDURE; Schema: pkg_receipts; Owner: postgres
--

CREATE PROCEDURE pkg_receipts.receipt_details_update(IN p_receipt_id bigint, IN p_product_id bigint, IN p_expected_quantity numeric, IN p_received_quantity numeric)
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- WALIDACJA KLUCZY
    IF p_receipt_id IS NULL OR p_receipt_id <= 0 THEN
        RAISE EXCEPTION 'Invalid receipt id: %', p_receipt_id;
    END IF;

    IF p_product_id IS NULL OR p_product_id <= 0 THEN
        RAISE EXCEPTION 'Invalid product id: %', p_product_id;
    END IF;

    -- WALIDACJA ILOŚCI
    IF p_expected_quantity IS NOT NULL AND p_expected_quantity < 0 THEN
        RAISE EXCEPTION 'Expected quantity must be >= 0';
    END IF;

    IF p_received_quantity IS NOT NULL AND p_received_quantity < 0 THEN
        RAISE EXCEPTION 'Received quantity must be >= 0';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM receipt_details
        WHERE receipt_id = p_receipt_id
          AND product_id = p_product_id
    ) THEN
        RAISE EXCEPTION
            'Receipt detail for receipt % and product % does not exist',
            p_receipt_id, p_product_id;
    END IF;

    UPDATE receipt_details
    SET expected_quantity = COALESCE(p_expected_quantity, expected_quantity),
        received_quantity = COALESCE(p_received_quantity, received_quantity)
    WHERE receipt_id = p_receipt_id
      AND product_id = p_product_id;
END;
$$;


ALTER PROCEDURE pkg_receipts.receipt_details_update(IN p_receipt_id bigint, IN p_product_id bigint, IN p_expected_quantity numeric, IN p_received_quantity numeric) OWNER TO postgres;

--
-- TOC entry 273 (class 1255 OID 25509)
-- Name: receipts_create(bigint, bigint, date, text, character varying); Type: PROCEDURE; Schema: pkg_receipts; Owner: postgres
--

CREATE PROCEDURE pkg_receipts.receipts_create(IN p_supplier_id bigint, IN p_employee_id bigint, IN p_receipt_date date, IN p_external_invoice_no text, IN p_status character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- WALIDACJA FK
    IF p_supplier_id IS NULL OR p_supplier_id <= 0 THEN
        RAISE EXCEPTION 'Invalid supplier id: %', p_supplier_id;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM suppliers WHERE supplier_id = p_supplier_id
    ) THEN
        RAISE EXCEPTION 'Supplier with id % does not exist', p_supplier_id;
    END IF;

    IF p_employee_id IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM employees WHERE employee_id = p_employee_id
    ) THEN
        RAISE EXCEPTION 'Employee with id % does not exist', p_employee_id;
    END IF;

    -- WALIDACJA DATY
    IF p_receipt_date IS NOT NULL AND p_receipt_date > CURRENT_DATE THEN
        RAISE EXCEPTION 'Receipt date cannot be in the future';
    END IF;

    -- WALIDACJA STATUSU
    IF p_status IS NULL OR trim(p_status) = '' THEN
        RAISE EXCEPTION 'Receipt status cannot be empty';
    END IF;

    INSERT INTO receipts(
        supplier_id,
        employee_id,
        receipt_date,
        external_invoice_no,
        status
    )
    VALUES (
        p_supplier_id,
        p_employee_id,
        COALESCE(p_receipt_date, CURRENT_DATE),
        p_external_invoice_no,
        p_status
    );

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Error while creating receipt: %', SQLERRM;
END;
$$;


ALTER PROCEDURE pkg_receipts.receipts_create(IN p_supplier_id bigint, IN p_employee_id bigint, IN p_receipt_date date, IN p_external_invoice_no text, IN p_status character varying) OWNER TO postgres;

--
-- TOC entry 316 (class 1255 OID 25510)
-- Name: receipts_delete(bigint); Type: PROCEDURE; Schema: pkg_receipts; Owner: postgres
--

CREATE PROCEDURE pkg_receipts.receipts_delete(IN p_receipt_id bigint)
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF p_receipt_id IS NULL OR p_receipt_id <= 0 THEN
        RAISE EXCEPTION 'Invalid receipt id: %', p_receipt_id;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM receipts WHERE receipt_id = p_receipt_id
    ) THEN
        RAISE EXCEPTION 'Receipt with id % does not exist', p_receipt_id;
    END IF;

    DELETE FROM receipts WHERE receipt_id = p_receipt_id;
END;
$$;


ALTER PROCEDURE pkg_receipts.receipts_delete(IN p_receipt_id bigint) OWNER TO postgres;

--
-- TOC entry 234 (class 1259 OID 25511)
-- Name: receipts; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.receipts (
    receipt_id bigint NOT NULL,
    supplier_id bigint NOT NULL,
    employee_id bigint,
    receipt_date date DEFAULT CURRENT_DATE,
    external_invoice_no text,
    status character varying(50) NOT NULL
);


ALTER TABLE public.receipts OWNER TO postgres;

--
-- TOC entry 301 (class 1255 OID 25517)
-- Name: receipts_read_all(); Type: FUNCTION; Schema: pkg_receipts; Owner: postgres
--

CREATE FUNCTION pkg_receipts.receipts_read_all() RETURNS SETOF public.receipts
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT * FROM receipts;

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Error while reading receipts: %', SQLERRM;
END;
$$;


ALTER FUNCTION pkg_receipts.receipts_read_all() OWNER TO postgres;

--
-- TOC entry 255 (class 1255 OID 25518)
-- Name: receipts_read_one(bigint); Type: FUNCTION; Schema: pkg_receipts; Owner: postgres
--

CREATE FUNCTION pkg_receipts.receipts_read_one(p_receipt_id bigint) RETURNS public.receipts
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_row receipts;
BEGIN
    IF p_receipt_id IS NULL OR p_receipt_id <= 0 THEN
        RAISE EXCEPTION 'Invalid receipt id: %', p_receipt_id;
    END IF;

    SELECT *
    INTO v_row
    FROM receipts
    WHERE receipt_id = p_receipt_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Receipt with id % not found', p_receipt_id;
    END IF;

    RETURN v_row;

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION
            'Error while reading receipt %: %',
            p_receipt_id, SQLERRM;
END;
$$;


ALTER FUNCTION pkg_receipts.receipts_read_one(p_receipt_id bigint) OWNER TO postgres;

--
-- TOC entry 319 (class 1255 OID 25519)
-- Name: receipts_update(bigint, bigint, bigint, date, text, character varying); Type: PROCEDURE; Schema: pkg_receipts; Owner: postgres
--

CREATE PROCEDURE pkg_receipts.receipts_update(IN p_receipt_id bigint, IN p_supplier_id bigint, IN p_employee_id bigint, IN p_receipt_date date, IN p_external_invoice_no text, IN p_status character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- WALIDACJA ID
    IF p_receipt_id IS NULL OR p_receipt_id <= 0 THEN
        RAISE EXCEPTION 'Invalid receipt id: %', p_receipt_id;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM receipts WHERE receipt_id = p_receipt_id
    ) THEN
        RAISE EXCEPTION 'Receipt with id % does not exist', p_receipt_id;
    END IF;

    -- FK
    IF p_supplier_id IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM suppliers WHERE supplier_id = p_supplier_id
    ) THEN
        RAISE EXCEPTION 'Supplier with id % does not exist', p_supplier_id;
    END IF;

    IF p_employee_id IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM employees WHERE employee_id = p_employee_id
    ) THEN
        RAISE EXCEPTION 'Employee with id % does not exist', p_employee_id;
    END IF;

    -- DATA
    IF p_receipt_date IS NOT NULL AND p_receipt_date > CURRENT_DATE THEN
        RAISE EXCEPTION 'Receipt date cannot be in the future';
    END IF;

    UPDATE receipts
    SET supplier_id         = COALESCE(p_supplier_id, supplier_id),
        employee_id         = COALESCE(p_employee_id, employee_id),
        receipt_date        = COALESCE(p_receipt_date, receipt_date),
        external_invoice_no = COALESCE(p_external_invoice_no, external_invoice_no),
        status              = COALESCE(p_status, status)
    WHERE receipt_id = p_receipt_id;
END;
$$;


ALTER PROCEDURE pkg_receipts.receipts_update(IN p_receipt_id bigint, IN p_supplier_id bigint, IN p_employee_id bigint, IN p_receipt_date date, IN p_external_invoice_no text, IN p_status character varying) OWNER TO postgres;

--
-- TOC entry 265 (class 1255 OID 25520)
-- Name: shipment_details_create(bigint, bigint, numeric); Type: PROCEDURE; Schema: pkg_shipments; Owner: postgres
--

CREATE PROCEDURE pkg_shipments.shipment_details_create(IN p_shipment_id bigint, IN p_product_id bigint, IN p_quantity_to_ship numeric)
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- WALIDACJA ID
    IF p_shipment_id IS NULL OR p_shipment_id <= 0 THEN
        RAISE EXCEPTION 'Invalid shipment id: %', p_shipment_id;
    END IF;

    IF p_product_id IS NULL OR p_product_id <= 0 THEN
        RAISE EXCEPTION 'Invalid product id: %', p_product_id;
    END IF;

    -- WALIDACJA ILOŚCI
    IF p_quantity_to_ship IS NULL OR p_quantity_to_ship < 0 THEN
        RAISE EXCEPTION 'Quantity to ship must be >= 0';
    END IF;

    -- FK
    IF NOT EXISTS (
        SELECT 1 FROM shipments WHERE shipment_id = p_shipment_id
    ) THEN
        RAISE EXCEPTION 'Shipment with id % does not exist', p_shipment_id;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM products WHERE product_id = p_product_id
    ) THEN
        RAISE EXCEPTION 'Product with id % does not exist', p_product_id;
    END IF;

    -- UNIKALNOŚĆ (PK ZŁOŻONY)
    IF EXISTS (
        SELECT 1
        FROM shipment_details
        WHERE shipment_id = p_shipment_id
          AND product_id  = p_product_id
    ) THEN
        RAISE EXCEPTION
            'Shipment detail for shipment % and product % already exists',
            p_shipment_id, p_product_id;
    END IF;

    INSERT INTO shipment_details(
        shipment_id,
        product_id,
        quantity_to_ship
    )
    VALUES (
        p_shipment_id,
        p_product_id,
        p_quantity_to_ship
    );

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Error while creating shipment detail: %', SQLERRM;
END;
$$;


ALTER PROCEDURE pkg_shipments.shipment_details_create(IN p_shipment_id bigint, IN p_product_id bigint, IN p_quantity_to_ship numeric) OWNER TO postgres;

--
-- TOC entry 297 (class 1255 OID 25521)
-- Name: shipment_details_delete(bigint, bigint); Type: PROCEDURE; Schema: pkg_shipments; Owner: postgres
--

CREATE PROCEDURE pkg_shipments.shipment_details_delete(IN p_shipment_id bigint, IN p_product_id bigint)
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF p_shipment_id IS NULL OR p_shipment_id <= 0 THEN
        RAISE EXCEPTION 'Invalid shipment id: %', p_shipment_id;
    END IF;

    IF p_product_id IS NULL OR p_product_id <= 0 THEN
        RAISE EXCEPTION 'Invalid product id: %', p_product_id;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM shipment_details
        WHERE shipment_id = p_shipment_id
          AND product_id  = p_product_id
    ) THEN
        RAISE EXCEPTION
            'Shipment detail for shipment % and product % does not exist',
            p_shipment_id, p_product_id;
    END IF;

    DELETE FROM shipment_details
    WHERE shipment_id = p_shipment_id
      AND product_id  = p_product_id;
END;
$$;


ALTER PROCEDURE pkg_shipments.shipment_details_delete(IN p_shipment_id bigint, IN p_product_id bigint) OWNER TO postgres;

--
-- TOC entry 235 (class 1259 OID 25522)
-- Name: shipment_details; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.shipment_details (
    shipment_id bigint NOT NULL,
    product_id bigint NOT NULL,
    quantity_to_ship numeric(14,3) NOT NULL,
    CONSTRAINT shipment_details_quantity_to_ship_check CHECK ((quantity_to_ship >= (0)::numeric))
);


ALTER TABLE public.shipment_details OWNER TO postgres;

--
-- TOC entry 313 (class 1255 OID 25526)
-- Name: shipment_details_read_all(); Type: FUNCTION; Schema: pkg_shipments; Owner: postgres
--

CREATE FUNCTION pkg_shipments.shipment_details_read_all() RETURNS SETOF public.shipment_details
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT * FROM shipment_details;

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Error while reading shipment details: %', SQLERRM;
END;
$$;


ALTER FUNCTION pkg_shipments.shipment_details_read_all() OWNER TO postgres;

--
-- TOC entry 280 (class 1255 OID 25527)
-- Name: shipment_details_read_one(bigint, bigint); Type: FUNCTION; Schema: pkg_shipments; Owner: postgres
--

CREATE FUNCTION pkg_shipments.shipment_details_read_one(p_shipment_id bigint, p_product_id bigint) RETURNS public.shipment_details
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_row shipment_details;
BEGIN
    IF p_shipment_id IS NULL OR p_shipment_id <= 0 THEN
        RAISE EXCEPTION 'Invalid shipment id: %', p_shipment_id;
    END IF;

    IF p_product_id IS NULL OR p_product_id <= 0 THEN
        RAISE EXCEPTION 'Invalid product id: %', p_product_id;
    END IF;

    SELECT *
    INTO v_row
    FROM shipment_details
    WHERE shipment_id = p_shipment_id
      AND product_id  = p_product_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'Shipment detail for shipment % and product % not found',
            p_shipment_id, p_product_id;
    END IF;

    RETURN v_row;

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION
            'Error while reading shipment detail (% , %): %',
            p_shipment_id, p_product_id, SQLERRM;
END;
$$;


ALTER FUNCTION pkg_shipments.shipment_details_read_one(p_shipment_id bigint, p_product_id bigint) OWNER TO postgres;

--
-- TOC entry 261 (class 1255 OID 25528)
-- Name: shipment_details_update(bigint, bigint, numeric); Type: PROCEDURE; Schema: pkg_shipments; Owner: postgres
--

CREATE PROCEDURE pkg_shipments.shipment_details_update(IN p_shipment_id bigint, IN p_product_id bigint, IN p_quantity_to_ship numeric)
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- WALIDACJA KLUCZY
    IF p_shipment_id IS NULL OR p_shipment_id <= 0 THEN
        RAISE EXCEPTION 'Invalid shipment id: %', p_shipment_id;
    END IF;

    IF p_product_id IS NULL OR p_product_id <= 0 THEN
        RAISE EXCEPTION 'Invalid product id: %', p_product_id;
    END IF;

    -- WALIDACJA ILOŚCI
    IF p_quantity_to_ship IS NOT NULL AND p_quantity_to_ship < 0 THEN
        RAISE EXCEPTION 'Quantity to ship must be >= 0';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM shipment_details
        WHERE shipment_id = p_shipment_id
          AND product_id  = p_product_id
    ) THEN
        RAISE EXCEPTION
            'Shipment detail for shipment % and product % does not exist',
            p_shipment_id, p_product_id;
    END IF;

    UPDATE shipment_details
    SET quantity_to_ship = COALESCE(p_quantity_to_ship, quantity_to_ship)
    WHERE shipment_id = p_shipment_id
      AND product_id  = p_product_id;
END;
$$;


ALTER PROCEDURE pkg_shipments.shipment_details_update(IN p_shipment_id bigint, IN p_product_id bigint, IN p_quantity_to_ship numeric) OWNER TO postgres;

--
-- TOC entry 309 (class 1255 OID 25529)
-- Name: shipments_create(bigint, bigint, date, text, character varying); Type: PROCEDURE; Schema: pkg_shipments; Owner: postgres
--

CREATE PROCEDURE pkg_shipments.shipments_create(IN p_client_id bigint, IN p_employee_id bigint, IN p_shipment_date date, IN p_client_order_no text, IN p_status character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- WALIDACJA FK
    IF p_client_id IS NULL OR p_client_id <= 0 THEN
        RAISE EXCEPTION 'Invalid client id: %', p_client_id;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM clients WHERE client_id = p_client_id
    ) THEN
        RAISE EXCEPTION 'Client with id % does not exist', p_client_id;
    END IF;

    IF p_employee_id IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM employees WHERE employee_id = p_employee_id
    ) THEN
        RAISE EXCEPTION 'Employee with id % does not exist', p_employee_id;
    END IF;

    -- WALIDACJA DATY
    IF p_shipment_date IS NOT NULL AND p_shipment_date > CURRENT_DATE THEN
        RAISE EXCEPTION 'Shipment date cannot be in the future';
    END IF;

    -- WALIDACJA STATUSU
    IF p_status IS NULL OR trim(p_status) = '' THEN
        RAISE EXCEPTION 'Shipment status cannot be empty';
    END IF;

    INSERT INTO shipments(
        client_id,
        employee_id,
        shipment_date,
        client_order_no,
        status
    )
    VALUES (
        p_client_id,
        p_employee_id,
        COALESCE(p_shipment_date, CURRENT_DATE),
        p_client_order_no,
        p_status
    );

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Error while creating shipment: %', SQLERRM;
END;
$$;


ALTER PROCEDURE pkg_shipments.shipments_create(IN p_client_id bigint, IN p_employee_id bigint, IN p_shipment_date date, IN p_client_order_no text, IN p_status character varying) OWNER TO postgres;

--
-- TOC entry 274 (class 1255 OID 25530)
-- Name: shipments_delete(bigint); Type: PROCEDURE; Schema: pkg_shipments; Owner: postgres
--

CREATE PROCEDURE pkg_shipments.shipments_delete(IN p_shipment_id bigint)
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF p_shipment_id IS NULL OR p_shipment_id <= 0 THEN
        RAISE EXCEPTION 'Invalid shipment id: %', p_shipment_id;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM shipments WHERE shipment_id = p_shipment_id
    ) THEN
        RAISE EXCEPTION 'Shipment with id % does not exist', p_shipment_id;
    END IF;

    DELETE FROM shipments WHERE shipment_id = p_shipment_id;
END;
$$;


ALTER PROCEDURE pkg_shipments.shipments_delete(IN p_shipment_id bigint) OWNER TO postgres;

--
-- TOC entry 236 (class 1259 OID 25531)
-- Name: shipments; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.shipments (
    shipment_id bigint NOT NULL,
    client_id bigint NOT NULL,
    employee_id bigint,
    shipment_date date DEFAULT CURRENT_DATE,
    client_order_no text,
    status character varying(50) NOT NULL
);


ALTER TABLE public.shipments OWNER TO postgres;

--
-- TOC entry 294 (class 1255 OID 25537)
-- Name: shipments_read_all(); Type: FUNCTION; Schema: pkg_shipments; Owner: postgres
--

CREATE FUNCTION pkg_shipments.shipments_read_all() RETURNS SETOF public.shipments
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT * FROM shipments;

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Error while reading shipments: %', SQLERRM;
END;
$$;


ALTER FUNCTION pkg_shipments.shipments_read_all() OWNER TO postgres;

--
-- TOC entry 318 (class 1255 OID 25538)
-- Name: shipments_read_one(bigint); Type: FUNCTION; Schema: pkg_shipments; Owner: postgres
--

CREATE FUNCTION pkg_shipments.shipments_read_one(p_shipment_id bigint) RETURNS public.shipments
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_row shipments;
BEGIN
    IF p_shipment_id IS NULL OR p_shipment_id <= 0 THEN
        RAISE EXCEPTION 'Invalid shipment id: %', p_shipment_id;
    END IF;

    SELECT *
    INTO v_row
    FROM shipments
    WHERE shipment_id = p_shipment_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Shipment with id % not found', p_shipment_id;
    END IF;

    RETURN v_row;

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION
            'Error while reading shipment %: %',
            p_shipment_id, SQLERRM;
END;
$$;


ALTER FUNCTION pkg_shipments.shipments_read_one(p_shipment_id bigint) OWNER TO postgres;

--
-- TOC entry 266 (class 1255 OID 25539)
-- Name: shipments_update(bigint, bigint, bigint, date, text, character varying); Type: PROCEDURE; Schema: pkg_shipments; Owner: postgres
--

CREATE PROCEDURE pkg_shipments.shipments_update(IN p_shipment_id bigint, IN p_client_id bigint, IN p_employee_id bigint, IN p_shipment_date date, IN p_client_order_no text, IN p_status character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- WALIDACJA ID
    IF p_shipment_id IS NULL OR p_shipment_id <= 0 THEN
        RAISE EXCEPTION 'Invalid shipment id: %', p_shipment_id;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM shipments WHERE shipment_id = p_shipment_id
    ) THEN
        RAISE EXCEPTION 'Shipment with id % does not exist', p_shipment_id;
    END IF;

    -- FK
    IF p_client_id IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM clients WHERE client_id = p_client_id
    ) THEN
        RAISE EXCEPTION 'Client with id % does not exist', p_client_id;
    END IF;

    IF p_employee_id IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM employees WHERE employee_id = p_employee_id
    ) THEN
        RAISE EXCEPTION 'Employee with id % does not exist', p_employee_id;
    END IF;

    -- DATA
    IF p_shipment_date IS NOT NULL AND p_shipment_date > CURRENT_DATE THEN
        RAISE EXCEPTION 'Shipment date cannot be in the future';
    END IF;

    UPDATE shipments
    SET client_id       = COALESCE(p_client_id, client_id),
        employee_id     = COALESCE(p_employee_id, employee_id),
        shipment_date   = COALESCE(p_shipment_date, shipment_date),
        client_order_no = COALESCE(p_client_order_no, client_order_no),
        status          = COALESCE(p_status, status)
    WHERE shipment_id = p_shipment_id;
END;
$$;


ALTER PROCEDURE pkg_shipments.shipments_update(IN p_shipment_id bigint, IN p_client_id bigint, IN p_employee_id bigint, IN p_shipment_date date, IN p_client_order_no text, IN p_status character varying) OWNER TO postgres;

--
-- TOC entry 256 (class 1255 OID 25540)
-- Name: suppliers_create(text, text, text, text, text); Type: PROCEDURE; Schema: pkg_suppliers; Owner: postgres
--

CREATE PROCEDURE pkg_suppliers.suppliers_create(IN p_company_name text, IN p_address text, IN p_phone text, IN p_email text, IN p_tax_id text)
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- WALIDACJE
    IF p_company_name IS NULL OR trim(p_company_name) = '' THEN
        RAISE EXCEPTION 'Company name cannot be empty';
    END IF;

    IF p_email IS NOT NULL AND position('@' IN p_email) = 0 THEN
        RAISE EXCEPTION 'Invalid email format: %', p_email;
    END IF;

    IF p_tax_id IS NOT NULL AND length(p_tax_id) < 10 THEN
        RAISE EXCEPTION 'Tax ID is too short';
    END IF;

    INSERT INTO suppliers(
        company_name,
        address,
        phone,
        email,
        tax_id
    )
    VALUES (
        p_company_name,
        p_address,
        p_phone,
        p_email,
        p_tax_id
    );

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Error while creating supplier: %', SQLERRM;
END;
$$;


ALTER PROCEDURE pkg_suppliers.suppliers_create(IN p_company_name text, IN p_address text, IN p_phone text, IN p_email text, IN p_tax_id text) OWNER TO postgres;

--
-- TOC entry 315 (class 1255 OID 25541)
-- Name: suppliers_delete(bigint); Type: PROCEDURE; Schema: pkg_suppliers; Owner: postgres
--

CREATE PROCEDURE pkg_suppliers.suppliers_delete(IN p_supplier_id bigint)
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF p_supplier_id IS NULL OR p_supplier_id <= 0 THEN
        RAISE EXCEPTION 'Invalid supplier id: %', p_supplier_id;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM suppliers WHERE supplier_id = p_supplier_id
    ) THEN
        RAISE EXCEPTION 'Supplier with id % does not exist', p_supplier_id;
    END IF;

    DELETE FROM suppliers WHERE supplier_id = p_supplier_id;

EXCEPTION
    WHEN foreign_key_violation THEN
        RAISE EXCEPTION
            'Cannot delete supplier %, it is used by products or receipts',
            p_supplier_id;
END;
$$;


ALTER PROCEDURE pkg_suppliers.suppliers_delete(IN p_supplier_id bigint) OWNER TO postgres;

--
-- TOC entry 237 (class 1259 OID 25542)
-- Name: suppliers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.suppliers (
    supplier_id bigint NOT NULL,
    company_name text NOT NULL,
    address text,
    phone text,
    email text,
    tax_id text
);


ALTER TABLE public.suppliers OWNER TO postgres;

--
-- TOC entry 252 (class 1255 OID 25547)
-- Name: suppliers_read_all(); Type: FUNCTION; Schema: pkg_suppliers; Owner: postgres
--

CREATE FUNCTION pkg_suppliers.suppliers_read_all() RETURNS SETOF public.suppliers
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT * FROM suppliers;

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Error while reading suppliers: %', SQLERRM;
END;
$$;


ALTER FUNCTION pkg_suppliers.suppliers_read_all() OWNER TO postgres;

--
-- TOC entry 322 (class 1255 OID 25548)
-- Name: suppliers_read_one(bigint); Type: FUNCTION; Schema: pkg_suppliers; Owner: postgres
--

CREATE FUNCTION pkg_suppliers.suppliers_read_one(p_supplier_id bigint) RETURNS public.suppliers
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_row suppliers;
BEGIN
    IF p_supplier_id IS NULL OR p_supplier_id <= 0 THEN
        RAISE EXCEPTION 'Invalid supplier id: %', p_supplier_id;
    END IF;

    SELECT *
    INTO v_row
    FROM suppliers
    WHERE supplier_id = p_supplier_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Supplier with id % not found', p_supplier_id;
    END IF;

    RETURN v_row;

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION
            'Error while reading supplier %: %',
            p_supplier_id, SQLERRM;
END;
$$;


ALTER FUNCTION pkg_suppliers.suppliers_read_one(p_supplier_id bigint) OWNER TO postgres;

--
-- TOC entry 283 (class 1255 OID 25549)
-- Name: suppliers_update(bigint, text, text, text, text, text); Type: PROCEDURE; Schema: pkg_suppliers; Owner: postgres
--

CREATE PROCEDURE pkg_suppliers.suppliers_update(IN p_supplier_id bigint, IN p_company_name text, IN p_address text, IN p_phone text, IN p_email text, IN p_tax_id text)
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- WALIDACJA ID
    IF p_supplier_id IS NULL OR p_supplier_id <= 0 THEN
        RAISE EXCEPTION 'Invalid supplier id: %', p_supplier_id;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM suppliers WHERE supplier_id = p_supplier_id
    ) THEN
        RAISE EXCEPTION 'Supplier with id % does not exist', p_supplier_id;
    END IF;

    IF p_email IS NOT NULL AND position('@' IN p_email) = 0 THEN
        RAISE EXCEPTION 'Invalid email format: %', p_email;
    END IF;

    UPDATE suppliers
    SET company_name = COALESCE(p_company_name, company_name),
        address      = COALESCE(p_address, address),
        phone        = COALESCE(p_phone, phone),
        email        = COALESCE(p_email, email),
        tax_id       = COALESCE(p_tax_id, tax_id)
    WHERE supplier_id = p_supplier_id;
END;
$$;


ALTER PROCEDURE pkg_suppliers.suppliers_update(IN p_supplier_id bigint, IN p_company_name text, IN p_address text, IN p_phone text, IN p_email text, IN p_tax_id text) OWNER TO postgres;

--
-- TOC entry 238 (class 1259 OID 25550)
-- Name: categories_category_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.categories_category_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.categories_category_id_seq OWNER TO postgres;

--
-- TOC entry 5112 (class 0 OID 0)
-- Dependencies: 238
-- Name: categories_category_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.categories_category_id_seq OWNED BY public.categories.category_id;


--
-- TOC entry 239 (class 1259 OID 25551)
-- Name: clients_client_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.clients_client_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.clients_client_id_seq OWNER TO postgres;

--
-- TOC entry 5113 (class 0 OID 0)
-- Dependencies: 239
-- Name: clients_client_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.clients_client_id_seq OWNED BY public.clients.client_id;


--
-- TOC entry 240 (class 1259 OID 25552)
-- Name: employees_employee_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.employees_employee_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.employees_employee_id_seq OWNER TO postgres;

--
-- TOC entry 5114 (class 0 OID 0)
-- Dependencies: 240
-- Name: employees_employee_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.employees_employee_id_seq OWNED BY public.employees.employee_id;


--
-- TOC entry 241 (class 1259 OID 25553)
-- Name: inventory_inventory_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.inventory_inventory_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.inventory_inventory_id_seq OWNER TO postgres;

--
-- TOC entry 5115 (class 0 OID 0)
-- Dependencies: 241
-- Name: inventory_inventory_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.inventory_inventory_id_seq OWNED BY public.inventory.inventory_id;


--
-- TOC entry 242 (class 1259 OID 25554)
-- Name: locations_location_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.locations_location_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.locations_location_id_seq OWNER TO postgres;

--
-- TOC entry 5116 (class 0 OID 0)
-- Dependencies: 242
-- Name: locations_location_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.locations_location_id_seq OWNED BY public.locations.location_id;


--
-- TOC entry 243 (class 1259 OID 25555)
-- Name: products_product_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.products_product_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.products_product_id_seq OWNER TO postgres;

--
-- TOC entry 5117 (class 0 OID 0)
-- Dependencies: 243
-- Name: products_product_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.products_product_id_seq OWNED BY public.products.product_id;


--
-- TOC entry 244 (class 1259 OID 25556)
-- Name: receipts_receipt_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.receipts_receipt_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.receipts_receipt_id_seq OWNER TO postgres;

--
-- TOC entry 5118 (class 0 OID 0)
-- Dependencies: 244
-- Name: receipts_receipt_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.receipts_receipt_id_seq OWNED BY public.receipts.receipt_id;


--
-- TOC entry 245 (class 1259 OID 25557)
-- Name: shipments_shipment_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.shipments_shipment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.shipments_shipment_id_seq OWNER TO postgres;

--
-- TOC entry 5119 (class 0 OID 0)
-- Dependencies: 245
-- Name: shipments_shipment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.shipments_shipment_id_seq OWNED BY public.shipments.shipment_id;


--
-- TOC entry 246 (class 1259 OID 25558)
-- Name: suppliers_supplier_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.suppliers_supplier_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.suppliers_supplier_id_seq OWNER TO postgres;

--
-- TOC entry 5120 (class 0 OID 0)
-- Dependencies: 246
-- Name: suppliers_supplier_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.suppliers_supplier_id_seq OWNED BY public.suppliers.supplier_id;


--
-- TOC entry 4865 (class 2604 OID 25559)
-- Name: categories category_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categories ALTER COLUMN category_id SET DEFAULT nextval('public.categories_category_id_seq'::regclass);


--
-- TOC entry 4866 (class 2604 OID 25560)
-- Name: clients client_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.clients ALTER COLUMN client_id SET DEFAULT nextval('public.clients_client_id_seq'::regclass);


--
-- TOC entry 4867 (class 2604 OID 25561)
-- Name: employees employee_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employees ALTER COLUMN employee_id SET DEFAULT nextval('public.employees_employee_id_seq'::regclass);


--
-- TOC entry 4868 (class 2604 OID 25562)
-- Name: inventory inventory_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inventory ALTER COLUMN inventory_id SET DEFAULT nextval('public.inventory_inventory_id_seq'::regclass);


--
-- TOC entry 4870 (class 2604 OID 25563)
-- Name: locations location_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.locations ALTER COLUMN location_id SET DEFAULT nextval('public.locations_location_id_seq'::regclass);


--
-- TOC entry 4871 (class 2604 OID 25564)
-- Name: products product_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products ALTER COLUMN product_id SET DEFAULT nextval('public.products_product_id_seq'::regclass);


--
-- TOC entry 4872 (class 2604 OID 25565)
-- Name: receipts receipt_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.receipts ALTER COLUMN receipt_id SET DEFAULT nextval('public.receipts_receipt_id_seq'::regclass);


--
-- TOC entry 4874 (class 2604 OID 25566)
-- Name: shipments shipment_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shipments ALTER COLUMN shipment_id SET DEFAULT nextval('public.shipments_shipment_id_seq'::regclass);


--
-- TOC entry 4876 (class 2604 OID 25567)
-- Name: suppliers supplier_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.suppliers ALTER COLUMN supplier_id SET DEFAULT nextval('public.suppliers_supplier_id_seq'::regclass);


--
-- TOC entry 5078 (class 0 OID 25439)
-- Dependencies: 227
-- Data for Name: categories; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.categories (category_id, name, description) FROM stdin;
1	Laptopy	Komputery przenośne do pracy i gier
2	Smartfony	Telefony mobilne różnych producentów
3	Akcesoria	Myszki, klawiatury, podkładki i inne dodatki
4	Tablety	Urządzenia mobilne z ekranem dotykowym
5	Monitory	Monitory komputerowe LED i LCD
7	Sieć	Urządzenia sieciowe: routery, switche, repeatery
8	Audio	Słuchawki, głośniki, mikrofony
9	Peryferia biurowe	Skanery, kamery internetowe, urządzenia biurowe
10	Gaming	Sprzęt i akcesoria dla graczy
11	Podzespoły PC	CPU, GPU, RAM, dyski i inne części komputerowe
12	Serwery	Sprzęt serwerowy, rackowy i komponenty
13	Oprogramowanie	Licencje Windows, Office, antywirusy
14	VR/AR	Sprzęt do rzeczywistości wirtualnej i rozszerzonej
15	Energia	Zasilacze UPS, baterie, przedłużacze
6	Drukarki	Drukarki laserowe, atramentowe i 3D
\.


--
-- TOC entry 5079 (class 0 OID 25449)
-- Dependencies: 228
-- Data for Name: clients; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.clients (client_id, company_name, delivery_address, phone, email, tax_id) FROM stdin;
1	Firma Alfa	ul. Zielona 1, Warszawa	222-333-444	kontakt@firmaalfa.pl	1231231230
2	Firma Beta	ul. Słoneczna 5, Kraków	333-444-555	biuro@firmabeta.pl	3213213210
3	Firma Gamma	ul. Leśna 10, Poznań	444-555-666	info@firmagamma.pl	4564564560
4	Delta Logistics	ul. Torowa 15, Gdańsk	555-666-777	kontakt@deltalogistics.pl	6546546540
5	Omega Trade	ul. Portowa 8, Gdynia	666-777-888	biuro@omegatrade.pl	7897897890
6	Nova Solutions	ul. Widok 3, Wrocław	777-888-999	info@novasolutions.pl	9879879870
7	StalTech	ul. Przemysłowa 20, Katowice	888-999-000	kontakt@staltech.pl	1112223334
8	AgroPol	ul. Rolna 7, Lublin	999-000-111	kontakt@agropol.pl	4445556667
9	LuxMedia	ul. Filmowa 14, Łódź	000-111-222	support@luxmedia.pl	5556667778
10	MediShop	ul. Zdrowa 9, Rzeszów	111-222-333	zamowienia@medishop.pl	6667778889
11	GreenEnergy	ul. Energetyczna 30, Opole	222-333-444	contact@greenenergy.pl	7778889990
12	BuildCorp	ul. Budowlana 18, Szczecin	333-444-555	biuro@buildcorp.pl	8889990001
13	SoftWareHouse	ul. Startowa 6, Białystok	444-555-666	support@softwarehouse.pl	9990001112
14	TransPol	ul. Transportowa 22, Toruń	555-666-777	kontakt@transpol.pl	0001112223
15	MegaMarket	ul. Handlowa 50, Bydgoszcz	666-777-888	biuro@megamarket.pl	1112223335
\.


--
-- TOC entry 5080 (class 0 OID 25459)
-- Dependencies: 229
-- Data for Name: employees; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.employees (employee_id, first_name, last_name, "position", hire_date, phone, email) FROM stdin;
1	Jan	Kowalski	Magazynier	2021-01-15	111-222-333	jan.kowalski@firma.pl
2	Anna	Nowak	Kierownik Magazynu	2020-03-10	222-333-444	anna.nowak@firma.pl
3	Piotr	Wiśniewski	Specjalista ds. logistyki	2022-06-10	333-444-555	piotr.wisniewski@firma.pl
4	Ewa	Zielińska	Magazynier	2023-02-05	444-555-666	ewa.zielinska@firma.pl
5	Tomasz	Lewandowski	Operator Wózka	2021-11-20	555-666-777	t.lewandowski@firma.pl
6	Karolina	Wójcik	Koordynator ds. wysyłek	2019-09-01	666-777-888	k.wojcik@firma.pl
7	Marcin	Kamiński	Specjalista IT	2020-12-12	777-888-999	m.kaminski@firma.pl
8	Agnieszka	Piotrowska	Magazynier	2022-10-18	888-999-000	a.piotrowska@firma.pl
9	Marek	Mazur	Pracownik biurowy	2023-01-22	999-000-111	m.mazur@firma.pl
10	Natalia	Kaczmarek	Specjalista ds. zakupów	2021-04-14	000-111-222	n.kaczmarek@firma.pl
11	Paweł	Król	Magazynier	2023-03-03	123-111-222	pawel.krol@firma.pl
12	Katarzyna	Sikora	Kontroler jakości	2021-12-11	234-222-333	k.sikora@firma.pl
13	Dominik	Ostrowski	Kierowca	2020-07-07	345-333-444	d.ostrowski@firma.pl
14	Olga	Adamczyk	Analityk logistyki	2019-05-25	456-444-555	o.adamczyk@firma.pl
15	Radosław	Duda	Magazynier	2022-08-09	567-555-666	r.duda@firma.pl
\.


--
-- TOC entry 5081 (class 0 OID 25469)
-- Dependencies: 230
-- Data for Name: inventory; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.inventory (inventory_id, product_id, location_id, quantity, last_updated) FROM stdin;
1	1	1	50.000	2025-12-10 22:42:30.662475+01
2	2	2	120.000	2025-12-10 22:42:30.662475+01
3	3	3	200.000	2025-12-10 22:42:30.662475+01
5	5	5	60.000	2025-12-10 22:42:30.662475+01
7	7	7	30.000	2025-12-10 22:42:30.662475+01
8	8	8	45.000	2025-12-10 22:42:30.662475+01
9	9	9	90.000	2025-12-10 22:42:30.662475+01
10	10	10	110.000	2025-12-10 22:42:30.662475+01
11	11	11	55.000	2025-12-10 22:42:30.662475+01
13	13	13	20.000	2025-12-10 22:42:30.662475+01
6	6	6	130.000	2026-01-21 14:03:58.083394+01
4	4	4	80.000	2026-01-21 16:51:20.086815+01
12	12	12	74.000	2026-01-21 16:55:21.181461+01
\.


--
-- TOC entry 5082 (class 0 OID 25479)
-- Dependencies: 231
-- Data for Name: locations; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.locations (location_id, location_code, location_type, max_capacity) FROM stdin;
1	LOC001	Magazyn Główny	1000.000
2	LOC002	Magazyn Boczny	500.000
3	LOC003	Magazyn Zapasowy	300.000
4	LOC004	Hala 1	1500.000
5	LOC005	Hala 2	1200.000
6	LOC006	Hala 3	1800.000
7	LOC007	Regal A1	200.000
8	LOC008	Regal A2	200.000
9	LOC009	Regal B1	250.000
10	LOC010	Regal B2	250.000
11	LOC011	Strefa Przyjęć	800.000
12	LOC012	Strefa Wysyłek	900.000
13	LOC013	Chłodnia	400.000
14	LOC014	Magazyn Chemiczny	350.000
15	LOC015	Strefa Kontroli	600.000
\.


--
-- TOC entry 5083 (class 0 OID 25490)
-- Dependencies: 232
-- Data for Name: products; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.products (product_id, sku, name, description, category_id, supplier_id, weight, dimensions) FROM stdin;
1	SKU001	Laptop Pro 15	Laptop 15\\" i7, 16GB RAM, 512GB SSD	1	1	2.300	35x24x2 cm
2	SKU002	Smartfon X10	Smartfon 6.5\\" AMOLED, 128GB	2	2	0.180	16x7x0.8 cm
3	SKU003	Mysz Gamingowa RGB	Mysz gamingowa 7200 DPI	3	3	0.120	12x6x4 cm
4	SKU004	Tablet Max 12	Tablet 12\\" 8GB RAM, 256GB	4	4	0.520	28x19x0.7 cm
5	SKU005	Monitor 27 UltraHD	Monitor 27\\" 4K HDR	5	5	4.200	62x37x6 cm
6	SKU006	Drukarka Laser A4	Drukarka laserowa monochromatyczna	6	6	8.000	40x32x25 cm
7	SKU007	Router WiFi 6 Plus	Dwuzakresowy router WiFi 6 3000 Mbps	7	7	0.350	22x15x5 cm
8	SKU008	Słuchawki BassBoost	Słuchawki nauszne z redukcją szumów	8	8	0.280	18x16x8 cm
9	SKU009	Kamera FullHD	Kamera internetowa 1080p	9	9	0.150	9x5x4 cm
10	SKU010	Klawiatura Mechaniczna	Klawiatura mechaniczna RGB	10	10	0.900	45x15x3 cm
11	SKU011	SSD 1TB NVMe	Dysk SSD PCIe 1TB	11	11	0.040	8x2x0.4 cm
12	SKU012	Serwer RACK 2U	Serwer 2U do szafy rack	12	12	12.500	60x45x10 cm
13	SKU013	Windows 11 Pro	Licencja cyfrowa Windows 11 Pro	13	13	0.001	N/A
14	SKU014	Zestaw VR Pro	Gogle VR + kontrolery	14	14	1.200	35x25x15 cm
15	SKU015	UPS 1200VA	Zasilacz awaryjny 1200VA	15	15	6.500	40x15x20 cm
\.


--
-- TOC entry 5084 (class 0 OID 25501)
-- Dependencies: 233
-- Data for Name: receipt_details; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.receipt_details (receipt_id, product_id, expected_quantity, received_quantity) FROM stdin;
1	1	50.000	50.000
2	2	100.000	95.000
3	3	75.000	75.000
5	5	120.000	118.000
6	6	40.000	38.000
7	7	30.000	30.000
8	8	90.000	90.000
9	9	110.000	108.000
10	10	55.000	55.000
11	11	200.000	199.000
12	12	70.000	70.000
13	13	45.000	45.000
14	14	85.000	80.000
15	15	150.000	150.000
4	4	60.000	50.000
\.


--
-- TOC entry 5085 (class 0 OID 25511)
-- Dependencies: 234
-- Data for Name: receipts; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.receipts (receipt_id, supplier_id, employee_id, receipt_date, external_invoice_no, status) FROM stdin;
1	1	1	2025-01-05	FV001	Oczekuje
2	2	3	2025-01-07	FV002	Zrealizowana
3	3	5	2025-01-10	FV003	Oczekuje
5	5	4	2025-01-14	FV005	Zrealizowana
6	6	6	2025-01-15	FV006	Oczekuje
7	7	7	2025-01-17	FV007	Oczekuje
8	8	8	2025-01-19	FV008	Zrealizowana
9	9	9	2025-01-20	FV009	Oczekuje
10	10	10	2025-01-21	FV010	Oczekuje
11	11	11	2025-01-22	FV011	Zrealizowana
12	12	12	2025-01-23	FV012	Oczekuje
13	13	13	2025-01-24	FV013	Oczekuje
14	14	14	2025-01-25	FV014	Zrealizowana
15	15	15	2025-01-27	FV015	Oczekuje
4	4	2	2025-01-12	FV004	Zrealizowana
\.


--
-- TOC entry 5086 (class 0 OID 25522)
-- Dependencies: 235
-- Data for Name: shipment_details; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.shipment_details (shipment_id, product_id, quantity_to_ship) FROM stdin;
1	1	10.000
4	4	15.000
5	5	25.000
7	7	18.000
8	8	22.000
9	9	35.000
10	10	14.000
11	11	28.000
12	12	17.000
13	13	40.000
14	14	11.000
15	15	32.000
18	6	9.000
6	6	11.000
2	2	18.000
\.


--
-- TOC entry 5087 (class 0 OID 25531)
-- Dependencies: 236
-- Data for Name: shipments; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.shipments (shipment_id, client_id, employee_id, shipment_date, client_order_no, status) FROM stdin;
1	1	1	2025-02-01	ZAM001	W trakcie
2	2	2	2025-02-02	ZAM002	Zrealizowana
3	3	3	2025-02-03	ZAM003	W trakcie
4	4	4	2025-02-04	ZAM004	W trakcie
5	5	5	2025-02-05	ZAM005	Zrealizowana
6	6	6	2025-02-06	ZAM006	W trakcie
7	7	7	2025-02-07	ZAM007	Zrealizowana
8	8	8	2025-02-08	ZAM008	W trakcie
9	9	9	2025-02-09	ZAM009	W trakcie
10	10	10	2025-02-10	ZAM010	Zrealizowana
11	11	11	2025-02-11	ZAM011	W trakcie
12	12	12	2025-02-12	ZAM012	W trakcie
13	13	13	2025-02-13	ZAM013	Zrealizowana
14	14	14	2025-02-14	ZAM014	W trakcie
15	15	15	2025-02-16	ZAM015	W trakcie
18	15	15	2025-02-16	ZAM015	W trakcie
101	1	2	2024-06-05	\N	SENT
102	2	2	2024-06-05	\N	SENT
103	3	2	2024-06-05	\N	SENT
104	4	2	2024-06-05	\N	SENT
105	5	2	2024-06-05	\N	SENT
106	6	2	2024-06-05	\N	SENT
107	7	2	2024-06-05	\N	SENT
108	8	2	2024-06-05	\N	SENT
109	9	2	2024-06-05	\N	SENT
110	10	2	2024-06-05	\N	SENT
111	11	2	2024-06-05	\N	SENT
112	12	2	2024-06-05	\N	SENT
201	1	3	2024-06-10	\N	SENT
202	2	3	2024-06-10	\N	SENT
203	3	3	2024-06-10	\N	SENT
204	4	3	2024-06-10	\N	SENT
205	5	3	2024-06-10	\N	SENT
206	6	3	2024-06-10	\N	SENT
207	7	3	2024-06-10	\N	SENT
208	8	3	2024-06-10	\N	SENT
209	9	3	2024-06-10	\N	SENT
210	10	3	2024-06-10	\N	SENT
\.


--
-- TOC entry 5088 (class 0 OID 25542)
-- Dependencies: 237
-- Data for Name: suppliers; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.suppliers (supplier_id, company_name, address, phone, email, tax_id) FROM stdin;
2	Tech Solutions	ul. Kwiatowa 5, Kraków	987-654-321	biuro@techsolutions.pl	0987654321
3	Gadget World	ul. Nowa 20, Poznań	555-666-777	info@gadgetworld.pl	1122334455
4	DigitalPoint	ul. Rolna 14, Gdańsk	222-444-666	kontakt@digitalpoint.pl	5566778899
5	ElectroHouse	ul. Długa 25, Łódź	333-555-777	biuro@electrohouse.pl	6677889900
7	KomputronikX	ul. Niska 4, Rzeszów	555-777-999	support@komputronikx.pl	8899001122
8	HardwareZone	ul. Śląska 7, Katowice	666-888-000	sklep@hardwarezone.pl	9900112233
10	MegaDevices	ul. Wiosenna 6, Bydgoszcz	888-000-222	info@megadevices.pl	1213141516
11	SmartParts	ul. Spacerowa 8, Szczecin	999-111-333	zamowienia@smartparts.pl	1314151617
12	TopComponents	ul. Przemysłowa 18, Olsztyn	111-333-555	kontakt@topcomponents.pl	1415161718
13	EuroElectro	ul. Portowa 3, Gdynia	222-444-666	biuro@euroelectro.pl	1516171819
14	NextGenSupply	ul. Mickiewicza 77, Toruń	333-555-777	support@nextgensupply.pl	1617181920
15	ITWarehouse	ul. Przewozowa 15, Opole	444-666-888	kontakt@itwarehouse.pl	1718192021
9	ProTechSupply	ul. Torowa 11, Lublin	777-999-111	kontakt@protechsupply.pl	1011121314
1	ABC Electronics	ul. Piękna 10, Warszawa	123-456-788	kontakt@abcelectronics.pl	1234567890
6	MobilePr	ul. Leśna 9, Wrocław	444-666-888	kontakt@mobilepro.pl	7788990011
\.


--
-- TOC entry 5121 (class 0 OID 0)
-- Dependencies: 238
-- Name: categories_category_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.categories_category_id_seq', 20, true);


--
-- TOC entry 5122 (class 0 OID 0)
-- Dependencies: 239
-- Name: clients_client_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.clients_client_id_seq', 16, true);


--
-- TOC entry 5123 (class 0 OID 0)
-- Dependencies: 240
-- Name: employees_employee_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.employees_employee_id_seq', 16, true);


--
-- TOC entry 5124 (class 0 OID 0)
-- Dependencies: 241
-- Name: inventory_inventory_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.inventory_inventory_id_seq', 16, true);


--
-- TOC entry 5125 (class 0 OID 0)
-- Dependencies: 242
-- Name: locations_location_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.locations_location_id_seq', 16, true);


--
-- TOC entry 5126 (class 0 OID 0)
-- Dependencies: 243
-- Name: products_product_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.products_product_id_seq', 16, true);


--
-- TOC entry 5127 (class 0 OID 0)
-- Dependencies: 244
-- Name: receipts_receipt_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.receipts_receipt_id_seq', 16, true);


--
-- TOC entry 5128 (class 0 OID 0)
-- Dependencies: 245
-- Name: shipments_shipment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.shipments_shipment_id_seq', 20, true);


--
-- TOC entry 5129 (class 0 OID 0)
-- Dependencies: 246
-- Name: suppliers_supplier_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.suppliers_supplier_id_seq', 18, true);


--
-- TOC entry 4884 (class 2606 OID 25569)
-- Name: categories categories_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_name_key UNIQUE (name);


--
-- TOC entry 4886 (class 2606 OID 25571)
-- Name: categories categories_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_pkey PRIMARY KEY (category_id);


--
-- TOC entry 4888 (class 2606 OID 25573)
-- Name: clients clients_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.clients
    ADD CONSTRAINT clients_pkey PRIMARY KEY (client_id);


--
-- TOC entry 4890 (class 2606 OID 25575)
-- Name: employees employees_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employees
    ADD CONSTRAINT employees_pkey PRIMARY KEY (employee_id);


--
-- TOC entry 4894 (class 2606 OID 25577)
-- Name: inventory inventory_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inventory
    ADD CONSTRAINT inventory_pkey PRIMARY KEY (inventory_id);


--
-- TOC entry 4898 (class 2606 OID 25579)
-- Name: locations locations_location_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.locations
    ADD CONSTRAINT locations_location_code_key UNIQUE (location_code);


--
-- TOC entry 4900 (class 2606 OID 25581)
-- Name: locations locations_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.locations
    ADD CONSTRAINT locations_pkey PRIMARY KEY (location_id);


--
-- TOC entry 4904 (class 2606 OID 25583)
-- Name: products products_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_pkey PRIMARY KEY (product_id);


--
-- TOC entry 4906 (class 2606 OID 25585)
-- Name: products products_sku_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_sku_key UNIQUE (sku);


--
-- TOC entry 4908 (class 2606 OID 25587)
-- Name: receipt_details receipt_details_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.receipt_details
    ADD CONSTRAINT receipt_details_pkey PRIMARY KEY (receipt_id, product_id);


--
-- TOC entry 4912 (class 2606 OID 25589)
-- Name: receipts receipts_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.receipts
    ADD CONSTRAINT receipts_pkey PRIMARY KEY (receipt_id);


--
-- TOC entry 4914 (class 2606 OID 25591)
-- Name: shipment_details shipment_details_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shipment_details
    ADD CONSTRAINT shipment_details_pkey PRIMARY KEY (shipment_id, product_id);


--
-- TOC entry 4918 (class 2606 OID 25593)
-- Name: shipments shipments_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shipments
    ADD CONSTRAINT shipments_pkey PRIMARY KEY (shipment_id);


--
-- TOC entry 4920 (class 2606 OID 25595)
-- Name: suppliers suppliers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.suppliers
    ADD CONSTRAINT suppliers_pkey PRIMARY KEY (supplier_id);


--
-- TOC entry 4896 (class 2606 OID 25597)
-- Name: inventory uq_inventory_product_location; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inventory
    ADD CONSTRAINT uq_inventory_product_location UNIQUE (product_id, location_id);


--
-- TOC entry 4891 (class 1259 OID 25598)
-- Name: idx_inventory_location; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_inventory_location ON public.inventory USING btree (location_id);


--
-- TOC entry 4892 (class 1259 OID 25599)
-- Name: idx_inventory_product; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_inventory_product ON public.inventory USING btree (product_id);


--
-- TOC entry 4901 (class 1259 OID 25600)
-- Name: idx_products_category; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_products_category ON public.products USING btree (category_id);


--
-- TOC entry 4902 (class 1259 OID 25601)
-- Name: idx_products_supplier; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_products_supplier ON public.products USING btree (supplier_id);


--
-- TOC entry 4909 (class 1259 OID 25602)
-- Name: idx_receipts_employee; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_receipts_employee ON public.receipts USING btree (employee_id);


--
-- TOC entry 4910 (class 1259 OID 25603)
-- Name: idx_receipts_supplier; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_receipts_supplier ON public.receipts USING btree (supplier_id);


--
-- TOC entry 4915 (class 1259 OID 25604)
-- Name: idx_shipments_client; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_shipments_client ON public.shipments USING btree (client_id);


--
-- TOC entry 4916 (class 1259 OID 25605)
-- Name: idx_shipments_employee; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_shipments_employee ON public.shipments USING btree (employee_id);


--
-- TOC entry 4921 (class 2606 OID 25606)
-- Name: inventory fk_inventory_location; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inventory
    ADD CONSTRAINT fk_inventory_location FOREIGN KEY (location_id) REFERENCES public.locations(location_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 4922 (class 2606 OID 25611)
-- Name: inventory fk_inventory_product; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inventory
    ADD CONSTRAINT fk_inventory_product FOREIGN KEY (product_id) REFERENCES public.products(product_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 4923 (class 2606 OID 25616)
-- Name: products fk_products_category; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES public.categories(category_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 4924 (class 2606 OID 25621)
-- Name: products fk_products_supplier; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT fk_products_supplier FOREIGN KEY (supplier_id) REFERENCES public.suppliers(supplier_id) ON UPDATE CASCADE ON DELETE SET NULL;


--
-- TOC entry 4925 (class 2606 OID 25626)
-- Name: receipt_details fk_receipt_details_product; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.receipt_details
    ADD CONSTRAINT fk_receipt_details_product FOREIGN KEY (product_id) REFERENCES public.products(product_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 4926 (class 2606 OID 25631)
-- Name: receipt_details fk_receipt_details_receipt; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.receipt_details
    ADD CONSTRAINT fk_receipt_details_receipt FOREIGN KEY (receipt_id) REFERENCES public.receipts(receipt_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 4927 (class 2606 OID 25636)
-- Name: receipts fk_receipts_employee; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.receipts
    ADD CONSTRAINT fk_receipts_employee FOREIGN KEY (employee_id) REFERENCES public.employees(employee_id) ON UPDATE CASCADE ON DELETE SET NULL;


--
-- TOC entry 4928 (class 2606 OID 25641)
-- Name: receipts fk_receipts_supplier; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.receipts
    ADD CONSTRAINT fk_receipts_supplier FOREIGN KEY (supplier_id) REFERENCES public.suppliers(supplier_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 4929 (class 2606 OID 25646)
-- Name: shipment_details fk_shipment_details_product; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shipment_details
    ADD CONSTRAINT fk_shipment_details_product FOREIGN KEY (product_id) REFERENCES public.products(product_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 4930 (class 2606 OID 25651)
-- Name: shipment_details fk_shipment_details_shipment; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shipment_details
    ADD CONSTRAINT fk_shipment_details_shipment FOREIGN KEY (shipment_id) REFERENCES public.shipments(shipment_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 4931 (class 2606 OID 25656)
-- Name: shipments fk_shipments_client; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shipments
    ADD CONSTRAINT fk_shipments_client FOREIGN KEY (client_id) REFERENCES public.clients(client_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 4932 (class 2606 OID 25661)
-- Name: shipments fk_shipments_employee; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shipments
    ADD CONSTRAINT fk_shipments_employee FOREIGN KEY (employee_id) REFERENCES public.employees(employee_id) ON UPDATE CASCADE ON DELETE SET NULL;


-- Completed on 2026-01-23 15:03:56

--
-- PostgreSQL database dump complete
--

