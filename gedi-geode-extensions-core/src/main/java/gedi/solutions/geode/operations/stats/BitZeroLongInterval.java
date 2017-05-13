package gedi.solutions.geode.operations.stats;

class BitZeroLongInterval extends BitZeroInterval {
    long bits;

    @Override
    int getMemoryUsed() {
      return super.getMemoryUsed() + 8;
    }

    @Override
    long getBits() {
      return bits;
    }

    BitZeroLongInterval(long bits, int count) {
      super(count);
      this.bits = bits;
    }
  }