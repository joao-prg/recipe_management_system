CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS public.users (
	id uuid DEFAULT uuid_generate_v4() NOT NULL,
	authority varchar(255) NULL,
	email varchar(255) NOT NULL,
	"password" varchar(255) NULL,
	CONSTRAINT uk_email UNIQUE (email),
	CONSTRAINT pk_users_id PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.recipes (
	id uuid DEFAULT uuid_generate_v4() NOT NULL,
	category varchar(255) NULL,
	"date" timestamp with time zone NULL,
	description varchar(255) NULL,
	"name" varchar(255) NULL,
	author_id uuid NOT NULL,
	CONSTRAINT pk_recipes_id PRIMARY KEY (id),
	CONSTRAINT fk_recipes_users FOREIGN KEY (author_id) REFERENCES public.users(id)
);

CREATE TABLE IF NOT EXISTS public.recipe_directions (
	recipe_id uuid NOT NULL,
	directions varchar(255) NULL,
	CONSTRAINT fk_recipe_directions_recipes FOREIGN KEY (recipe_id) REFERENCES public.recipes(id)
);

CREATE TABLE IF NOT EXISTS public.recipe_ingredients (
	recipe_id uuid NOT NULL,
	ingredients varchar(255) NULL,
	CONSTRAINT fk_recipe_ingredients_recipes FOREIGN KEY (recipe_id) REFERENCES public.recipes(id)
);
