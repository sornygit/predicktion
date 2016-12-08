#Predicktion

##So what's this about?
Well, the inspiration comes from the 2016 presidential election, where social media was
overflowing with experts on geopolitics, fortunetellers and what not.

Apparantly though, the 'experts' and most of mainstream media had severly underestimated
how fed up people are with the current state of things and voted in a sexist bigot with
megalomania. Still better than crooked Killary, some may say. Well, that's up to you.

Anyway, wouldn't it have felt nice to have been able to make your predictions, and then when
they came through rub them in people's faces and say I TOLD YOU SO, in style?

**That's exactly the purpose of this application.** Enjoy.

##How do I give it a spin?
Your first option is to use the website the application is deployed to. It's not open for anyone yet though, sorry.

So...

1. The project is a standard Maven project using Java 8 with sourcelevel 8. Import it into your IDE.

2. Create an application.properties file in src/main/resources (or put it on your server's classpath). 
Use properties/template.application.properties as a template.

3. Run the class PredicktionApplication.

4. Browse to your server. Sign in with user "admin" password "admin!" the first time (change the password right away, is my tip ;p).

5. Make predicktions, have fun.

##Techy gore

Application is based on Spring Boot with Thymeleaf as template engine (first time trying it out properly).

Javascript-stuff: jQuery (and a bunch of plugins)

CSS-stuff: Bootstrap

Java 8+

## NOTE

To build a WAR to deploy on Tomcat, disable the "run-local" default Maven profile and instead activate the "deploy-on-tomcat" one.