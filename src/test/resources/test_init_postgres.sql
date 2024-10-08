CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE public.users (
	id uuid DEFAULT uuid_generate_v4() NOT NULL,
	authority varchar(255) NULL,
	email varchar(255) NOT NULL,
	"password" varchar(255) NULL,
	CONSTRAINT uk_email UNIQUE (email),
	CONSTRAINT pk_users_id PRIMARY KEY (id)
);

CREATE TABLE public.recipes (
	id uuid DEFAULT uuid_generate_v4() NOT NULL,
	category varchar(255) NULL,
	"date" timestamp with time zone NULL,
	description varchar(255) NULL,
	"name" varchar(255) NULL,
	author_id uuid NOT NULL,
	CONSTRAINT pk_recipes_id PRIMARY KEY (id),
	CONSTRAINT fk_recipes_users FOREIGN KEY (author_id) REFERENCES public.users(id)
);

CREATE TABLE public.recipe_directions (
	recipe_id uuid NOT NULL,
	directions varchar(255) NULL,
	CONSTRAINT fk_recipe_directions_recipes FOREIGN KEY (recipe_id) REFERENCES public.recipes(id)
);

CREATE TABLE public.recipe_ingredients (
	recipe_id uuid NOT NULL,
	ingredients varchar(255) NULL,
	CONSTRAINT fk_recipe_ingredients_recipes FOREIGN KEY (recipe_id) REFERENCES public.recipes(id)
);


INSERT INTO users (id, authority, email, password) VALUES
('59eca8ae-d82d-44d3-9cb1-920a1cbcc9f7'::uuid, 'ROLE_USER', 'test@test.com', '$2a$10$BfxOooCYtVNIqEolcWO6w.OpfklK3eOn7i5Fmx0NU.YhyNrJLgIjO'),
('b0731234-5159-414f-83a4-7711693b6cd8'::uuid, 'ROLE_USER', 'test2@test.com', '$2a$10$V7xMCTAXmB0sl/3.g0cEv.GCwv4ynCvkUjpqpnhYKUwJbRKB6sfFO');

INSERT INTO recipes (id, category, "date", description,"name",author_id) VALUES
('4b437406-abf0-459a-a22f-5c65f1cf102a'::uuid, 'soup','2024-08-17 22:06:43.2372+00','onion soup','onion soup','59eca8ae-d82d-44d3-9cb1-920a1cbcc9f7'::uuid);

INSERT INTO public.recipe_directions (recipe_id,directions) VALUES
('4b437406-abf0-459a-a22f-5c65f1cf102a'::uuid,'make the soup');

INSERT INTO public.recipe_ingredients (recipe_id,ingredients) VALUES
('4b437406-abf0-459a-a22f-5c65f1cf102a'::uuid,'onion');