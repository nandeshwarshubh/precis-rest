package ind.shubhamn.precisrest.rest.config;

/*
@Configuration
public class CustomWebSecurityConfigurerAdapter
        extends WebSecurityConfigurerAdapter {

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        corsConfiguration.setAllowedHeaders(List.of("*"));
//        corsConfiguration.setAllowedOrigins(List.of("*"));
//        corsConfiguration.setAllowedMethods(List.of("*"));
//        corsConfiguration.setAllowCredentials(true);
//        corsConfiguration.setExposedHeaders(List.of("*"));
//
//        // You can customize the following part based on your project, it's only a sample
//        http.authorizeRequests().antMatchers("/**").permitAll().anyRequest()
//                .authenticated().and().csrf().disable().cors().configurationSource(request -> corsConfiguration);
//
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // You can customize the following part based on your project, it's only a sample
        http.authorizeRequests().antMatchers("/**").permitAll().anyRequest()
                .authenticated().and().csrf().disable().addFilterBefore(new SimpleCorsFilter(), CorsFilter.class);

    }


}
@Configuration
public class CustomWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedMethods("*");
            }
        };
    }
}
*/