package com.example.thandbag.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;

@AnalyzeClasses(packages = "com.example.thandbag")
public class PackageTest {

    /**
     * 1. controller 패키지의 클래스는 repository 패키지의 클래스를 직접 호출할 수 없다.
     * 2. repository 패키지의 클래스는 service에서만 호출할 수 있다.
     * 3. 순환참조가 없어야 한다.
     */

    // 1번 테스트
//    @ArchTest
//    ArchRule controllerRule = noClasses().that().haveSimpleNameEndingWith("Test")
//            .and().resideOutsideOfPackage("..controller..")
//            .should().accessClassesThat().resideInAPackage("..repository..");
}
