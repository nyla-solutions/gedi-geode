package gedi.solutions.geode.operations.stats;

class BitNonZeroLongLongInterval extends BitNonZeroInterval {
    long bits;
    long interval;

    @Override
    int getMemoryUsed() {
      return super.getMemoryUsed() + 16;
    }

    @Override
    long getBits() {
      return this.bits;
    }

    @Override
    long getInterval() {
      return this.interval;
    }

    BitNonZeroLongLongInterval(long bits, long interval, int count) {
      super(count);
      this.bits = bits;
      this.interval = interval;
    }
  }