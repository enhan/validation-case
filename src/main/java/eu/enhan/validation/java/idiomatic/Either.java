package eu.enhan.validation.java.idiomatic;

/**
 *
 */
public abstract class Either<L, R> {

    private Either() {
    }

    public static final class Left<L, R> extends Either<L, R> {
        public final L left;

        public Left(L left) {
            this.left = left;
        }
    }

    public static final class Right<L, R> extends Either<L, R> {

        public final R right;

        public Right(R right) {
            this.right = right;
        }
    }

}
