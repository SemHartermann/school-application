    package co.inventorsoft.academy.schoolapplication.dto;

    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.NotNull;
    import lombok.AccessLevel;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import lombok.experimental.FieldDefaults;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public class PostDto {
        @NotNull
        Long id;
        @NotNull
        Long authorId;
        @NotNull
        String postTitle;
        @NotBlank
        String content;

    }
