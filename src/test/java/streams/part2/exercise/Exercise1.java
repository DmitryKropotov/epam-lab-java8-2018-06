package streams.part2.exercise;

import lambda.data.Employee;
import lambda.data.JobHistoryEntry;
import lambda.data.Person;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;

@SuppressWarnings({"ConstantConditions", "unused"})
public class Exercise1 {

    @Test
    public void calcTotalYearsSpentInEpam() {
        List<Employee> employees = getEmployees();

        // TODO реализация
        Stream<JobHistoryEntry> jobHistoryEntryStream = employees.stream().flatMap(Exercise1::employeeToJobHistoryEntryStream);
        Long hours = employees.stream().flatMap(Exercise1::employeeToJobHistoryEntryStream).
        filter(jobHistoryEntry -> jobHistoryEntry.getEmployer().equals("EPAM")).mapToLong(JobHistoryEntry::getDuration).sum();

        assertEquals(19, hours.longValue());
    }

    private static Stream<JobHistoryEntry> employeeToJobHistoryEntryStream(Employee employee) {
       return employee.getJobHistory().stream();
    }

    @Test
    public void findPersonsWithQaExperience() {
        List<Employee> employees = getEmployees();

        // TODO реализация
        Set<Person> workedAsQa = employees.stream().filter(Employee -> Employee.getJobHistory().stream().
                map(JobHistoryEntry::getPosition).anyMatch(string -> string.equals("QA"))).map(Employee::getPerson).collect(Collectors.toSet());

        assertEquals(new HashSet<>(Arrays.asList(
                employees.get(2).getPerson(),
                employees.get(4).getPerson(),
                employees.get(5).getPerson()
        )), workedAsQa);
    }

    @Test
    public void composeFullNamesOfEmployeesUsingLineSeparatorAsDelimiter() {
        List<Employee> employees = getEmployees();

        // TODO реализация
        String result = employees.stream().map(Employee::getPerson).map(Person::getFullName).collect(Collectors.joining("\n"));

        assertEquals("Иван Мельников\n"
                + "Александр Дементьев\n"
                + "Дмитрий Осинов\n"
                + "Анна Светличная\n"
                + "Игорь Толмачёв\n"
                + "Иван Александров", result);
    }

    @Test
    @SuppressWarnings("Duplicates")
    public void groupPersonsByFirstPositionUsingToMap() {
        List<Employee> employees = getEmployees();

        // TODO реализация
        Map<String, Set<Person>> result = employees.stream().collect(Collectors.
                toMap(employee -> employee.getJobHistory().get(0).getPosition(), employee -> new HashSet<>(
                Collections.singletonList(employee.getPerson())), (leftPersons, rightPersons) -> {
                leftPersons.addAll(rightPersons);
                return leftPersons;
        }));

        Map<String, Set<Person>> expected = new HashMap<>();
        expected.put("QA", new HashSet<>(Arrays.asList(employees.get(2).getPerson(), employees.get(5).getPerson())));
        expected.put("dev", Collections.singleton(employees.get(0).getPerson()));
        expected.put("tester", new HashSet<>(Arrays.asList(
                employees.get(1).getPerson(),
                employees.get(3).getPerson(),
                employees.get(4).getPerson()))
        );
        assertEquals(expected, result);
    }

    @Test
    @SuppressWarnings("Duplicates")
    public void groupPersonsByFirstPositionUsingGroupingByCollector() {
        List<Employee> employees = getEmployees();

        // TODO реализация
        Map<String, Set<Person>> result = employees.stream().map(Exercise1::employeeToPersonFirstPositionPair).
           collect(Collectors.groupingBy(PersonPositionPair::getPosition, mapping(PersonPositionPair::getPerson, toSet())));


        Map<String, Set<Person>> expected = new HashMap<>();
        expected.put("QA", new HashSet<>(Arrays.asList(employees.get(2).getPerson(), employees.get(5).getPerson())));
        expected.put("dev", Collections.singleton(employees.get(0).getPerson()));
        expected.put("tester", new HashSet<>(Arrays.asList(
                employees.get(1).getPerson(),
                employees.get(3).getPerson(),
                employees.get(4).getPerson()))
        );
        assertEquals(expected, result);
    }

    private static class PersonPositionPair {
        Person person;
        String position;

        PersonPositionPair(Person person, String position) {
            this.person = person;
            this.position = position;
        }

        Person getPerson() {
            return person;
        }

        String getPosition() {
            return position;
        }
    }

    private static PersonPositionPair employeeToPersonFirstPositionPair(Employee employee){
        Person person = employee.getPerson();
        String position = employee.getJobHistory().get(0).getPosition();
        return new PersonPositionPair(person, position);
    }

    private static HashMap<String, Set<Person>> mergeMaps(HashMap<String, Set<Person>> left, HashMap<String, Set<Person>> right) {
        HashMap<String, Set<Person>> result = new HashMap<>(left);
        right.forEach((key, value) -> result.merge(key, value, (resultSet, rightSet) -> {
            resultSet.addAll(rightSet);
            return resultSet;
        }));
        return result;
    }

    private static List<Employee> getEmployees() {
        return Arrays.asList(
                new Employee(
                        new Person("Иван", "Мельников", 30),
                        Arrays.asList(
                                new JobHistoryEntry(2, "dev", "EPAM"),
                                new JobHistoryEntry(1, "dev", "google")
                        )),
                new Employee(
                        new Person("Александр", "Дементьев", 28),
                        Arrays.asList(
                                new JobHistoryEntry(1, "tester", "EPAM"),
                                new JobHistoryEntry(1, "dev", "EPAM"),
                                new JobHistoryEntry(1, "dev", "google")
                        )),
                new Employee(
                        new Person("Дмитрий", "Осинов", 40),
                        Arrays.asList(
                                new JobHistoryEntry(3, "QA", "yandex"),
                                new JobHistoryEntry(1, "QA", "mail.ru"),
                                new JobHistoryEntry(1, "dev", "mail.ru")
                        )),
                new Employee(
                        new Person("Анна", "Светличная", 21),
                        Collections.singletonList(
                                new JobHistoryEntry(1, "tester", "T-Systems")
                        )),
                new Employee(
                        new Person("Игорь", "Толмачёв", 50),
                        Arrays.asList(
                                new JobHistoryEntry(5, "tester", "EPAM"),
                                new JobHistoryEntry(6, "QA", "EPAM")
                        )),
                new Employee(
                        new Person("Иван", "Александров", 33),
                        Arrays.asList(
                                new JobHistoryEntry(2, "QA", "T-Systems"),
                                new JobHistoryEntry(3, "QA", "EPAM"),
                                new JobHistoryEntry(1, "dev", "EPAM")
                        ))
        );
    }
}
