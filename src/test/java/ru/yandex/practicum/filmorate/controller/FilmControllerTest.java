package ru.yandex.practicum.filmorate.controller;

class FilmControllerTest {
 /*   FilmController filmController;
    UserController userController;

    @BeforeEach
    public void start() {
        UserStorage userStorage = new UserStorage();
        FilmStorageFilm filmStorage = new FilmStorageFilm();
        LikesStorage likeStorage;
        FilmService filmService = new FilmService(filmStorage,userStorage, likeStorage);
        UserService userService = new UserService(userStorage, friendsStorage);
        filmController = new FilmController(filmService);
        userController = new UserController(userService);
    }

    @Test
    void createUnlimitReleasedFilm_shouldShowErrorMessage() {
        Film film = Film.builder()
                .id(1)
                .name("Movie")
                .description("Interesting")
                .releaseDate(LocalDate.now().minusYears(200))
                .duration(180)
                .build();
        ValidationException e = assertThrows(ValidationException.class, () -> filmController.create(film));

        assertEquals("The object form is filled in incorrectly", e.getMessage());
    }

    @Test
    void updateUnlimitReleasedFilm_shouldShowErrorMessage() {
        Film film = Film.builder()
                .name("Movie")
                .description("Interesting")
                .releaseDate(LocalDate.now().minusYears(43))
                .duration(240)
                .build();
        filmController.create(film);
        Film filmUpdate = Film.builder()
                .name("Movie")
                .description("Interesting")
                .releaseDate(LocalDate.now().minusYears(230))
                .duration(193)
                .build();

        ValidationException e = assertThrows(ValidationException.class, () -> filmController.update(filmUpdate));

        assertEquals("Object update form was filled out incorrectly", e.getMessage());
    }

    @Test
    void addLikeToFilm_ShouldAddLikeToFilm() {
        Film film = Film.builder()
                .id(1)
                .name("Movie")
                .description("Interesting")
                .releaseDate(LocalDate.now().minusYears(43))
                .duration(240)
                .build();
        filmController.create(film);
        User user1 = new User(1, "sashajaaa@yandex.ru", "sashajaaa", "", LocalDate.now().minusYears(35));
        userController.create(user1);
        filmController.addLike(1,1);

        assertEquals(1, film.getLikes().size());
    }*/
}