package net.frank.mc3ver;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Architekturregeln, die bisher nur als Konvention galten.
 * *Logic-Klassen bleiben ohne laufendes Spiel testbar: Wertetypen von Minecraft (BlockPos, DyeColor,
 * ResourceKey) sind erlaubt, Welt, Server, Client, Entities, Blöcke und Fabric nicht.
 */
class ArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void importClasses() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("net.frank.mc3ver");
    }

    @Test
    void logicClassesStayFreeOfGameRuntime() {
        ArchRule rule = noClasses()
                .that()
                .haveSimpleNameEndingWith("Logic")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "net.fabricmc..",
                        "net.minecraft.client..",
                        "net.minecraft.server..",
                        "net.minecraft.world.entity..",
                        "net.minecraft.world.level.block..")
                .orShould()
                .dependOnClassesThat()
                .haveFullyQualifiedName("net.minecraft.world.level.Level")
                .because("*Logic-Klassen müssen ohne laufendes Spiel testbar bleiben");
        rule.check(classes);
    }

    @Test
    void logicClassesDoNotUseModBlocksOrItems() {
        ArchRule rule = noClasses()
                .that()
                .haveSimpleNameEndingWith("Logic")
                .should()
                .dependOnClassesThat()
                .haveSimpleNameEndingWith("Block")
                .orShould()
                .dependOnClassesThat()
                .haveSimpleNameEndingWith("Item")
                .orShould()
                .dependOnClassesThat()
                .haveSimpleNameStartingWith("Mod")
                .because("Block-, Item- und Mod*-Klassen rufen Logic auf, nicht umgekehrt");
        rule.check(classes);
    }
}
