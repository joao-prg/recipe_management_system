CREATE TABLE public.users (
	id bigserial NOT NULL,
	authority varchar(255) NULL,
	email varchar(255) NOT NULL,
	"password" varchar(255) NULL,
	CONSTRAINT uk_6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email),
	CONSTRAINT users_pkey PRIMARY KEY (id)
);

CREATE TABLE public.recipes (
	id bigserial NOT NULL,
	category varchar(255) NULL,
	"date" timestamp NULL,
	description varchar(255) NULL,
	"name" varchar(255) NULL,
	author_id int8 NULL,
	CONSTRAINT recipes_pkey PRIMARY KEY (id),
	CONSTRAINT fkhcd6j9baovkdrh8gu9my8lco5 FOREIGN KEY (author_id) REFERENCES public.users(id)
);

CREATE TABLE public.recipe_directions (
	recipe_id int8 NOT NULL,
	directions varchar(255) NULL,
	CONSTRAINT fkhvcukpdw0n8nnnwcdkw16v44s FOREIGN KEY (recipe_id) REFERENCES public.recipes(id)
);

CREATE TABLE public.recipe_ingredients (
	recipe_id int8 NOT NULL,
	ingredients varchar(255) NULL,
	CONSTRAINT fkcqlw8sor5ut10xsuj3jnttkc FOREIGN KEY (recipe_id) REFERENCES public.recipes(id)
);


INSERT INTO users (authority, email, password) VALUES
('ROLE_USER', 'test@test.com', '$2a$10$BfxOooCYtVNIqEolcWO6w.OpfklK3eOn7i5Fmx0NU.YhyNrJLgIjO'),
('ROLE_USER', 'test2@test.com', '$2a$10$V7xMCTAXmB0sl/3.g0cEv.GCwv4ynCvkUjpqpnhYKUwJbRKB6sfFO');

INSERT INTO recipes (category, "date", description,"name",author_id) VALUES
('soup','2024-08-17 22:06:43.2372','onion soup','onion soup',1);

INSERT INTO public.recipe_directions (recipe_id,directions) VALUES
(1,'make the soup');

INSERT INTO public.recipe_ingredients (recipe_id,ingredients) VALUES
(1,'onion');


