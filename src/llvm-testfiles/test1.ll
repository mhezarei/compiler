; ModuleID = 'test1.ll'
source_filename = "test1.cpp"
target datalayout = "e-m:e-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

; Function Attrs: noinline norecurse nounwind optnone uwtable
define i32 @main() #0 {
  %1 = alloca i32, align 4
  %2 = alloca i8, align 1
  %3 = alloca i8, align 1
  %4 = alloca double, align 8
  %5 = alloca i32, align 4
  %6 = alloca i32, align 4
  %7 = alloca i64, align 8
  %8 = alloca float, align 4
  %9 = alloca float, align 4
  store i32 0, i32* %1, align 4
  store i8 115, i8* %3, align 1
  store double 9.000000e+00, double* %4, align 8
  %10 = load i32, i32* %5, align 4
  %11 = add nsw i32 2, %10
  %12 = sitofp i32 %11 to float
  store float %12, float* %9, align 4
  %13 = load float, float* %8, align 4
  %14 = fmul float 1.000000e+00, %13
  %15 = load float, float* %9, align 4
  %16 = fadd float %14, %15
  %17 = fptosi float %16 to i32
  store i32 %17, i32* %5, align 4
  %18 = load double, double* %4, align 8
  %19 = fcmp une double %18, 0.000000e+00
  %20 = zext i1 %19 to i8
  store i8 %20, i8* %2, align 1
  %21 = load i64, i64* %7, align 8
  %22 = load i32, i32* %6, align 4
  %23 = sext i32 %22 to i64
  %24 = icmp eq i64 %21, %23
  br i1 %24, label %25, label %26

; <label>:25:                                     ; preds = %0
  store float 3.000000e+00, float* %8, align 4
  br label %27

; <label>:26:                                     ; preds = %0
  store float 4.000000e+00, float* %8, align 4
  br label %27

; <label>:27:                                     ; preds = %26, %25
  %28 = load float, float* %9, align 4
  %29 = fptosi float %28 to i32
  ret i32 %29
}

attributes #0 = { noinline norecurse nounwind optnone uwtable "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-jump-tables"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "no-trapping-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+fxsr,+mmx,+sse,+sse2,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }

!llvm.module.flags = !{!0}
!llvm.ident = !{!1}

!0 = !{i32 1, !"wchar_size", i32 4}
!1 = !{!"clang version 6.0.0-1ubuntu2 (tags/RELEASE_600/final)"}
