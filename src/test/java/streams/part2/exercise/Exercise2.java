package streams.part2.exercise;

import lambda.data.Employee;
import lambda.data.JobHistoryEntry;
import lambda.data.Person;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("ConstantConditions")
public class Exercise2 {

    private static class PairPersonT<T> {
        private Person person;
        private T tParameter;

        PairPersonT(Person person, T tParameter) {
            this.person = person;
            this.tParameter = tParameter;
        }

        Person getPerson() {
            return person;
        }

        T getTParameter() {
            return tParameter;
        }
    }

    /**
     * Преобразовать список сотрудников в отображение [компания -> множество людей, когда-либо работавших в этой компании].
     *
     * Входные данные:
     * [
     *     {
     *         {Иван Мельников 30},
     *         [
     *             {2, dev, "EPAM"},
     *             {1, dev, "google"}
     *         ]
     *     },
     *     {
     *         {Александр Дементьев 28},
     *         [
     *             {2, tester, "EPAM"},
     *             {1, dev, "EPAM"},
     *             {1, dev, "google"}
     *         ]
     *     },
     *     {
     *         {Дмитрий Осинов 40},
     *         [
     *             {3, QA, "yandex"},
     *             {1, QA, "EPAM"},
     *             {1, dev, "mail.ru"}
     *         ]
     *     },
     *     {
     *         {Анна Светличная 21},
     *         [
     *             {1, tester, "T-Systems"}
     *         ]
     *     }
     * ]
     *
     * Выходные данные:
     * [
     *    "EPAM" -> [
     *       {Иван Мельников 30},
     *       {Александр Дементьев 28},
     *       {Дмитрий Осинов 40}
     *    ],
     *    "google" -> [
     *       {Иван Мельников 30},
     *       {Александр Дементьев 28}
     *    ],
     *    "yandex" -> [ {Дмитрий Осинов 40} ]
     *    "mail.ru" -> [ {Дмитрий Осинов 40} ]
     *    "T-Systems" -> [ {Анна Светличная 21} ]
     * ]
     */
    @Test
    public void employersStuffList() {
        List<Employee> employees = getEmployees();

        Map<String, Set<Person>> result = employees.stream().flatMap(Exercise2::employeeToPairsEmployerPerson).
                collect(Collectors.toMap(PairPersonT::getTParameter(),
                        pairEmployerPerson -> new HashSet<>(Collections.singletonList(pairEmployerPerson.getPerson())), (set1, set2) -> {
              set1.addAll(set2);
              return set1;
            }));

        Map<String, Set<Person>> expected = new HashMap<>();
        expected.put("yandex", new HashSet<>(Collections.singletonList(employees.get(2).getPerson())));
        expected.put("mail.ru", new HashSet<>(Collections.singletonList(employees.get(2).getPerson())));
        expected.put("EPAM", new HashSet<>(Arrays.asList(
            employees.get(0).getPerson(),
            employees.get(1).getPerson(),
            employees.get(4).getPerson(),
            employees.get(5).getPerson()
        )));
        expected.put("google", new HashSet<>(Arrays.asList(
            employees.get(0).getPerson(),
            employees.get(1).getPerson()
        )));
        expected.put("T-Systems", new HashSet<>(Arrays.asList(
            employees.get(3).getPerson(),
            employees.get(5).getPerson()
        )));
        assertEquals(expected, result);
    }

    private static Stream<PairPersonT<Person>> employeeToPairsEmployerPerson(Employee employee) {
        Person person = employee.getPerson();
        return employee.getJobHistory().stream().map(JobHistoryEntry::getEmployer).map(employer -> new PairPersonT(person, employer));
    }

    /**
     * Преобразовать список сотрудников в отображение [компания -> множество людей, начавших свою карьеру в этой компании].
     *
     * Пример.
     *
     * Входные данные:
     * [
     *     {
     *         {Иван Мельников 30},
     *         [
     *             {2, dev, "EPAM"},
     *             {1, dev, "google"}
     *         ]
     *     },
     *     {
     *         {Александр Дементьев 28},
     *         [
     *             {2, tester, "EPAM"},
     *             {1, dev, "EPAM"},
     *             {1, dev, "google"}
     *         ]
     *     },
     *     {
     *         {Дмитрий Осинов 40},
     *         [
     *             {3, QA, "yandex"},
     *             {1, QA, "EPAM"},
     *             {1, dev, "mail.ru"}
     *         ]
     *     },
     *     {
     *         {Анна Светличная 21},
     *         [
     *             {1, tester, "T-Systems"}
     *         ]
     *     }
     * ]
     *
     * Выходные данные:
     * [
     *    "EPAM" -> [
     *       {Иван Мельников 30},
     *       {Александр Дементьев 28}
     *    ],
     *    "yandex" -> [ {Дмитрий Осинов 40} ]
     *    "T-Systems" -> [ {Анна Светличная 21} ]
     * ]
     */
    @Test
    public void indexByFirstEmployer() {
        List<Employee> employees = getEmployees();

        Map<String, Set<Person>> result = employees.stream().collect(Collectors.toMap(Exercise2::employeeToFirstCompany,
                employee -> new HashSet<>(Collections.singletonList(employee.getPerson())), (set1, set2) -> {
                    set1.addAll(set2);
                    return set1;
                }));

        Map<String, Set<Person>> expected = new HashMap<>();
        expected.put("yandex", new HashSet<>(Collections.singletonList(employees.get(2).getPerson())));
        expected.put("EPAM", new HashSet<>(Arrays.asList(
            employees.get(0).getPerson(),
            employees.get(1).getPerson(),
            employees.get(4).getPerson()
        )));
        expected.put("T-Systems", new HashSet<>(Arrays.asList(
            employees.get(3).getPerson(),
            employees.get(5).getPerson()
        )));
        assertEquals(expected, result);
    }

    private static String employeeToFirstCompany(Employee employee){
        return employee.getJobHistory().get(0).getEmployer();
    }

    /**
     * Преобразовать список сотрудников в отображение [компания -> сотрудник, суммарно проработавший в ней наибольшее время].
     * Гарантируется, что такой сотрудник будет один.
     */
    @Test
    public void greatestExperiencePerEmployer() {
        List<Employee> employees = getEmployees();

        Map<String, Person> collect = employees.stream().flatMap(Exercise2::employeeToTriplePersonEmployerDuration).
                map(Exercise2::triplePersonEmployerDurationToPairEmployerPairPersonDuration).
                collect(Collectors.toMap(PairEmployerPairPersonDuration::getEmployer,
                        new PairPersonT<Integer>(TripleEmployerPersonDuration::getPerson, TripleEmployerPersonDuration::getDuration),
                        (pairPersonT1, pairPersonT2) -> pairPersonT1.getTParameter() >  pairPersonT2.getTParameter() ?  pairPersonT1 : pairPersonT2)).
                collect(Collectors.toMap(TripleEmployerPersonDuration::getEmployer);

        Map<String, Person> expected = new HashMap<>();
        expected.put("EPAM", employees.get(4).getPerson());
        expected.put("google", employees.get(1).getPerson());
        expected.put("yandex", employees.get(2).getPerson());
        expected.put("mail.ru", employees.get(2).getPerson());
        expected.put("T-Systems", employees.get(5).getPerson());
        assertEquals(expected, collect);
    }

    private static class TripleEmployerPersonDuration {
        private String employer;
        private Person person;
        private int duration;

        TripleEmployerPersonDuration(String employer, Person person, int duration) {
            this.employer = employer;
            this.person = person;
            this.duration = duration;
        }

        public Person getPerson() {
            return person;
        }

        String getEmployer() {
            return employer;
        }

        public int getDuration() {
            return duration;
        }
    }

    private static Stream<TripleEmployerPersonDuration> employeeToTriplePersonEmployerDuration(Employee employee) {
        Person person = employee.getPerson();
        return employee.getJobHistory().stream().
                map(jobHistoryEntry ->
                        new TripleEmployerPersonDuration(jobHistoryEntry.getEmployer(),person, jobHistoryEntry.getDuration()));
    }

    private class PairEmployerPairPersonDuration {
        private String employer;
        private PairPersonT<Integer> pairPersonDuration;

        public PairEmployerPairPersonDuration(String employer, PairPersonT<Integer> pairPersonDuration) {
            this.employer = employer;
            this.pairPersonDuration = pairPersonDuration;
        }

        public String getEmployer() {
            return employer;
        }

        public PairPersonT<Integer> getPairPersonDuration() {
            return pairPersonDuration;
        }
    }

    private static PairEmployerPairPersonDuration triplePersonEmployerDurationToPairEmployerPairPersonDuration(TripleEmployerPersonDuration employerPersonDuration) {
        return new PairEmployerPairPersonDuration(employerPersonDuration.employer, new PairPersonT<>(employerPersonDuration.person, employerPersonDuration.duration))
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
                                new JobHistoryEntry(2, "dev", "EPAM"),
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