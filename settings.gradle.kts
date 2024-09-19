pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        //포트원 결제
        maven { setUrl("https://jitpack.io") }
        //카카오로그인
        maven { setUrl ("https://devrepo.kakao.com/nexus/content/groups/public/") } // 카카오 로그인

    }
}

rootProject.name = "TestSpringRestApp"
include(":app")
