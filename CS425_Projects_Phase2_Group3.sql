DROP TABLE IF EXISTS public.session_manage;
DROP TABLE IF EXISTS public.order_product;
DROP TABLE IF EXISTS public.product;
DROP TABLE IF EXISTS public.installation;
DROP TABLE IF EXISTS public.sales_order;
DROP TABLE IF EXISTS public.customer;
DROP TABLE IF EXISTS public.system_user;

-- Table: public.system_user

CREATE TABLE IF NOT EXISTS public.system_user
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    create_time timestamp without time zone,
    created_by bigint,
    first_name character varying(255) COLLATE pg_catalog."default",
    last_name character varying(255) COLLATE pg_catalog."default",
    password character varying(255) COLLATE pg_catalog."default",
    phone character varying(255) COLLATE pg_catalog."default",
    privilege character varying(255) COLLATE pg_catalog."default",
    update_time timestamp without time zone,
    updated_by bigint,
    username character varying(255) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT system_user_pkey PRIMARY KEY (id),
    CONSTRAINT uk_74y7xiqrvp39wycn0ron4xq4h UNIQUE (username)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.system_user
    OWNER to postgres;

-- Table: public.customer

CREATE TABLE IF NOT EXISTS public.customer
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    address character varying(255) COLLATE pg_catalog."default",
    create_time timestamp without time zone,
    created_by bigint,
    name character varying(255) COLLATE pg_catalog."default",
    phone character varying(255) COLLATE pg_catalog."default",
    reward_points real,
    update_time timestamp without time zone,
    updated_by bigint,
    CONSTRAINT customer_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.customer
    OWNER to postgres;

-- Table: public.sales_order

CREATE TABLE IF NOT EXISTS public.sales_order
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    create_time timestamp without time zone,
    created_by bigint,
    payment_method character varying(50) COLLATE pg_catalog."default",
    update_time timestamp without time zone,
    updated_by bigint,
    customer_id bigint,
    system_user_id bigint NOT NULL,
    CONSTRAINT sales_order_pkey PRIMARY KEY (id),
    CONSTRAINT fkjgnjpwmcqq2r3ypews4nw3e3m FOREIGN KEY (system_user_id)
        REFERENCES public.system_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkqqe3xj99rblvm5n0h0cp48gsa FOREIGN KEY (customer_id)
        REFERENCES public.customer (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.sales_order
    OWNER to postgres;

-- Table: public.installation

CREATE TABLE IF NOT EXISTS public.installation
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    cost real,
    create_time timestamp without time zone,
    created_by bigint,
    date character varying(255) COLLATE pg_catalog."default",
    from_time character varying(255) COLLATE pg_catalog."default",
    price real,
    status character varying(255) COLLATE pg_catalog."default",
    to_time character varying(255) COLLATE pg_catalog."default",
    update_time timestamp without time zone,
    updated_by bigint,
    customer_id bigint,
    user_id bigint,
    CONSTRAINT installation_pkey PRIMARY KEY (id),
    CONSTRAINT fk11s9fc0j4lppn8bcoa2w38y37 FOREIGN KEY (user_id)
        REFERENCES public.system_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk7fykwe8hm1hhlgfpv3pl1woqh FOREIGN KEY (customer_id)
        REFERENCES public.customer (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.installation
    OWNER to postgres;

-- Table: public.product

CREATE TABLE IF NOT EXISTS public.product
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    category character varying(255) COLLATE pg_catalog."default",
    cost real,
    create_time timestamp without time zone,
    created_by bigint,
    name character varying(255) COLLATE pg_catalog."default",
    price real,
    stock_cnt bigint,
    update_time timestamp without time zone,
    updated_by bigint,
    CONSTRAINT product_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.product
    OWNER to postgres;

-- Table: public.order_product

CREATE TABLE IF NOT EXISTS public.order_product
(
    create_time timestamp without time zone,
    created_by bigint,
    quantity integer,
    update_time timestamp without time zone,
    updated_by bigint,
    order_id bigint,
    product_id bigint,
    CONSTRAINT contains_pkey PRIMARY KEY (product_id, order_id),
    CONSTRAINT fkhnfgqyjx3i80qoymrssls3kno FOREIGN KEY (product_id)
        REFERENCES public.product (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fks769yiwjbix3pllf0swopp8qe FOREIGN KEY (order_id)
        REFERENCES public.sales_order (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.order_product
    OWNER to postgres;

-- Table: public.session_manage

CREATE TABLE IF NOT EXISTS public.session_manage
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    create_time timestamp without time zone,
    created_by bigint,
    status character varying(255) COLLATE pg_catalog."default",
    update_time timestamp without time zone,
    updated_by bigint,
    system_user_id bigint NOT NULL,
    CONSTRAINT session_manage_pkey PRIMARY KEY (id),
    CONSTRAINT fklvrceunpy9dedae5ve32i9q3t FOREIGN KEY (system_user_id)
        REFERENCES public.system_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.session_manage
    OWNER to postgres;